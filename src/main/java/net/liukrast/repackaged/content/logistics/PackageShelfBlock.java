package net.liukrast.repackaged.content.logistics;

import com.simibubi.create.content.logistics.packager.PackagerBlock;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import net.liukrast.repackaged.registry.RepackagedBlockEntityTypes;
import net.liukrast.repackaged.registry.RepackagedBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.Locale;

@NonnullDefault
public class PackageShelfBlock extends PackagerBlock {
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);
    public PackageShelfBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        var state = super.getStateForPlacement(context);
        if(state == null) return null;

        BlockState stateBelow = level.getBlockState(pos.below());
        if (stateBelow.getBlock() instanceof PackageShelfBlock) {
            return state.setValue(TYPE, Type.TOP);
        }

        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context) || level.getBlockState(pos.above()).is(this)) {
            return state.setValue(TYPE, Type.BOTTOM);
        }

        return null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(stack.is(RepackagedBlocks.PACKAGE_SHELF.asItem()))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        BlockPos posBelow = pos.below();
        BlockState stateBelow = level.getBlockState(posBelow);

        if (state.getValue(TYPE) == Type.TOP && stateBelow.getBlock() instanceof PackageShelfBlock) {
            if (stateBelow.getValue(TYPE) == Type.TOP) {
                 level.setBlock(posBelow, stateBelow.setValue(TYPE, Type.MIDDLE), 3);
            }
        } else if (state.getValue(TYPE) == Type.BOTTOM) {
            boolean flag = level.getBlockState(pos.above()).is(this);
            level.setBlock(pos.above(), state.setValue(TYPE, flag ? Type.MIDDLE : Type.TOP), 3);
            level.removeBlockEntity(pos.above());
        }
        var controller = getController(level, pos);
        if(controller != null) controller.updateShelfSize();
    }

    @Override
    public void onRemove(BlockState pState, Level level, BlockPos pos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, level, pos, pNewState, pIsMoving);
        if(level.isClientSide) return;
        var controller = getController(level, pos);
        if(controller != null) controller.updateShelfSize();
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            Type type = state.getValue(TYPE);
            if (type == Type.TOP) {
                BlockPos posBelow = pos.below();
                BlockState stateBelow = level.getBlockState(posBelow);
                if (stateBelow.is(this) && stateBelow.getValue(TYPE) == Type.BOTTOM) {
                    level.setBlock(posBelow, Blocks.AIR.defaultBlockState(), 35);
                }
            } else {
                BlockPos posAbove = pos.above();
                BlockState stateAbove = level.getBlockState(posAbove);
                if(player.isCreative()) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
                }
                if(stateAbove.is(this) && stateAbove.getValue(TYPE) == Type.TOP) {
                    level.setBlock(posAbove, Blocks.AIR.defaultBlockState(), 35);
                }

            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    private PackageShelfBlockEntity getController(Level level, BlockPos pos) {
        BlockPos current = pos;
        while (level.getBlockState(current).getBlock() instanceof PackageShelfBlock) {
            BlockState state = level.getBlockState(current);
            if (state.getValue(TYPE) == Type.BOTTOM) {
                BlockEntity be = level.getBlockEntity(current);
                if (be instanceof PackageShelfBlockEntity pbe) {
                    return pbe;
                }
                break;
            }
            current = current.below();
        }
        return null;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        var world = context.getLevel();
        var pos = context.getClickedPos();
        var y = context.getClickLocation().y - pos.getY();
        var state1 = world.getBlockState(y < 0.5 ? pos.below() : pos.above());
        if(state1.is(this)) {
            if(state.getValue(TYPE) == Type.MIDDLE && state1.getValue(TYPE) == Type.MIDDLE) {
                if(y < 0.5) {
                    world.setBlock(pos, state.setValue(TYPE, Type.BOTTOM), 3);
                    world.setBlock(pos.below(), state.setValue(TYPE, Type.TOP), 3);
                } else {
                    world.setBlock(pos, state.setValue(TYPE, Type.TOP), 3);
                    world.setBlock(pos.above(), state.setValue(TYPE, Type.BOTTOM), 3);
                }
                return InteractionResult.SUCCESS;
            } else {
                if(y < 0.5) {
                    if(state.getValue(TYPE) == Type.BOTTOM) {
                        world.setBlock(pos, state.setValue(TYPE, Type.MIDDLE), 3);
                        world.removeBlockEntity(pos);
                        world.setBlock(pos.below(), state.setValue(TYPE, Type.MIDDLE), 3);
                        return InteractionResult.SUCCESS;
                    }
                } else {
                    if(state.getValue(TYPE) == Type.TOP) {
                        world.setBlock(pos, state.setValue(TYPE, Type.MIDDLE), 3);
                        world.setBlock(pos.above(), state.setValue(TYPE, Type.MIDDLE), 3);
                        world.removeBlockEntity(pos.above());
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }

        if (state.getValue(TYPE) != Type.BOTTOM) return InteractionResult.PASS;

        Direction clickedFace = context.getClickedFace();
        Direction currentFacing = state.getValue(FACING);

        if (clickedFace == Direction.DOWN) {
            world.setBlock(pos, state.setValue(FACING, currentFacing.getClockWise()),3);
            return InteractionResult.SUCCESS;
        }

        if (currentFacing == clickedFace.getClockWise())
            world.setBlock(pos, state.setValue(FACING, Direction.UP), 3);
        else if (currentFacing == Direction.UP)
            world.setBlock(pos, state.setValue(FACING, clickedFace.getCounterClockWise()), 3);
        else if (currentFacing == clickedFace.getCounterClockWise())
            world.setBlock(pos, state.setValue(FACING, clickedFace.getClockWise()), 3);
        else return InteractionResult.PASS;

        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP && state.getValue(TYPE) == Type.MIDDLE && !neighborState.is(this)) {
            return state.setValue(TYPE, Type.TOP);
        }

        if (direction == Direction.DOWN && state.getValue(TYPE) == Type.MIDDLE && !neighborState.is(this)) {
            return state.setValue(TYPE, Type.BOTTOM);
        }

        if (state.getValue(TYPE) == Type.BOTTOM && direction == Direction.UP && !neighborState.is(this)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public BlockEntityType<? extends PackagerBlockEntity> getBlockEntityType() {
        return RepackagedBlockEntityTypes.PACKAGE_SHELF.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, TYPE);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(TYPE) == Type.BOTTOM ? super.newBlockEntity(pos, state) : null;
    }

    public enum Type implements StringRepresentable {
        BOTTOM, MIDDLE, TOP;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        if (!level.isClientSide) {
            Type type = state.getValue(TYPE);
            if (type == Type.TOP) {
                BlockPos posBelow = pos.below();
                BlockState stateBelow = level.getBlockState(posBelow);
                if (stateBelow.is(this) && stateBelow.getValue(TYPE) == Type.BOTTOM) {
                    level.setBlock(posBelow, Blocks.AIR.defaultBlockState(), 35);
                }
            } else {
                BlockPos posAbove = pos.above();
                BlockState stateAbove = level.getBlockState(posAbove);
                if(stateAbove.is(this) && stateAbove.getValue(TYPE) == Type.TOP) {
                    level.setBlock(posAbove, Blocks.AIR.defaultBlockState(), 35);
                }
            }
        }
        return super.onSneakWrenched(state, context);
    }
}
