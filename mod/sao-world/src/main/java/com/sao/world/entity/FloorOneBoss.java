package com.sao.world.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class FloorOneBoss extends Zombie {
    private int phase = 1; // 1: normal, 2: enraged (<50%), 3: berserk (<25%)
    private boolean phaseTwoAdds = false;
    private boolean phaseThreeAdds = false;
    private int dashCooldown = 0;
    private int cleaveCooldown = 0;
    private int quakeCooldown = 40;
    private int summonCooldown = 120;

    public FloorOneBoss(EntityType<? extends Zombie> type, Level level) {
        super(type, level);
        this.xpReward = 200;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @Nullable net.minecraft.nbt.CompoundTag tag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData, tag);
        this.setCustomName(Component.literal("Illfang the Kobold Lord"));
        this.setCustomNameVisible(true);
        this.setItemSlot(EquipmentSlot.MAINHAND, Items.NETHERITE_SWORD.getDefaultInstance());
        this.setItemSlot(EquipmentSlot.OFFHAND, Items.SHIELD.getDefaultInstance());
        this.setPersistenceRequired();
        return data;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            updatePhase();
            handleDash();
            handleCleave();
            handleQuake();
            handleSummonPressure();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ATTACK_SPEED, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 2.0D);
    }

    private void handleDash() {
        if (dashCooldown > 0) dashCooldown--;
        if (dashCooldown > 0) return;
        if (this.getTarget() != null) {
            Vec3 dir = new Vec3(this.getTarget().getX() - this.getX(), 0, this.getTarget().getZ() - this.getZ()).normalize();
            double dashScale = switch (phase) {
                case 3 -> 1.1D;
                case 2 -> 0.9D;
                default -> 0.7D;
            };
            this.setDeltaMovement(dir.scale(dashScale).add(0, 0.25D, 0));
            this.hasImpulse = true;
            dashCooldown = switch (phase) {
                case 3 -> 45;
                case 2 -> 60;
                default -> 85;
            };
        }
    }

    private void handleCleave() {
        if (cleaveCooldown > 0) cleaveCooldown--;
        if (cleaveCooldown > 0) return;
        if (this.getTarget() != null && this.distanceTo(this.getTarget()) < 3.5F) {
            float dmg = switch (phase) {
                case 3 -> 14.0F;
                case 2 -> 10.0F;
                default -> 6.0F;
            };
            List<LivingEntity> hits = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3.8D), livingTarget());
            for (LivingEntity hit : hits) {
                hit.hurt(this.damageSources().mobAttack(this), dmg);
                Vec3 away = hit.position().vectorTo(this.position()).normalize();
                hit.knockback(0.6D + (0.1D * phase), away.x, away.z);
                hit.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40 + (phase * 20), 0));
            }
            cleaveCooldown = switch (phase) {
                case 3 -> 55;
                case 2 -> 70;
                default -> 90;
            };
        }
    }

    private void handleQuake() {
        if (quakeCooldown > 0) quakeCooldown--;
        if (quakeCooldown > 0) return;
        if (this.getTarget() == null) return;

        double radius = phase == 3 ? 5.5D : (phase == 2 ? 4.5D : 4.0D);
        float dmg = phase == 3 ? 12.0F : (phase == 2 ? 8.0F : 6.0F);
        AABB area = this.getBoundingBox().inflate(radius);
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 20, 1.0, 0.3, 1.0, 0.2);
        }
        for (LivingEntity hit : this.level().getEntitiesOfClass(LivingEntity.class, area, livingTarget())) {
            hit.hurt(this.damageSources().mobAttack(this), dmg);
            hit.knockback(0.4D + 0.1D * phase, this.getX() - hit.getX(), this.getZ() - hit.getZ());
            hit.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60 + (phase * 20), 1));
        }
        quakeCooldown = switch (phase) {
            case 3 -> 80;
            case 2 -> 100;
            default -> 130;
        };
    }

    private void handleSummonPressure() {
        if (!(this.level() instanceof ServerLevel server)) return;
        if (summonCooldown > 0) summonCooldown--;
        if (summonCooldown > 0) return;
        if (this.getTarget() == null) return;

        long nearbyAdds = server.getEntitiesOfClass(Zombie.class, this.getBoundingBox().inflate(12.0D),
                z -> z != this).size();
        if (nearbyAdds > 8) {
            summonCooldown = 40;
            return;
        }

        int count = phase == 3 ? 4 : 2;
        spawnAdds(server, count, phase == 3);
        summonCooldown = phase == 3 ? 180 : 220;
    }

    private void updatePhase() {
        float ratio = this.getHealth() / this.getMaxHealth();
        int desired = ratio <= 0.25f ? 3 : (ratio <= 0.5f ? 2 : 1);
        if (desired == phase) return;
        phase = desired;

        AttributeInstance dmg = this.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance spd = this.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        if (dmg != null) {
            dmg.setBaseValue(switch (phase) {
                case 3 -> 16.0D;
                case 2 -> 13.0D;
                default -> 10.0D;
            });
        }
        if (spd != null) {
            spd.setBaseValue(switch (phase) {
                case 3 -> 0.42D;
                case 2 -> 0.36D;
                default -> 0.32D;
            });
        }
        if (armor != null) {
            armor.setBaseValue(phase >= 2 ? 8.0D : 6.0D);
        }

        if (phase >= 2 && !phaseTwoAdds && this.level() instanceof ServerLevel server) {
            phaseTwoAdds = true;
            spawnAdds(server, 3, false);
        }
        if (phase >= 3 && !phaseThreeAdds && this.level() instanceof ServerLevel server) {
            phaseThreeAdds = true;
            spawnAdds(server, 2, true);
        }
    }

    private void spawnAdds(ServerLevel server, int count, boolean elite) {
        for (int i = 0; i < count; i++) {
            Zombie add = new Zombie(EntityType.ZOMBIE, server);
            add.setItemSlot(EquipmentSlot.MAINHAND, (elite ? Items.IRON_SWORD : Items.STONE_SWORD).getDefaultInstance());
            add.setPos(this.getX() + (server.random.nextDouble() - 0.5D) * 5.0D,
                    this.getY(),
                    this.getZ() + (server.random.nextDouble() - 0.5D) * 5.0D);
            add.setPersistenceRequired();
            add.getAttribute(Attributes.MAX_HEALTH).setBaseValue(elite ? 30.0D : 24.0D);
            add.setHealth(add.getMaxHealth());
            if (this.getTarget() != null) {
                add.setTarget(this.getTarget());
            }
            server.addFreshEntity(add);
        }
    }

    private Predicate<LivingEntity> livingTarget() {
        return e -> e.isAlive() && e != this && !(e instanceof FloorOneBoss);
    }
}
