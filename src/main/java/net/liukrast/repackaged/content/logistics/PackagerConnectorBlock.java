package net.liukrast.repackaged.content.logistics;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.liukrast.repackaged.registry.RepackagedBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NonnullDefault
public class PackagerConnectorBlock extends DirectionalBlock implements IBE<PackagerConnectorBlockEntity>, IWrenchable {
    public static final DirectionProperty POINTING = DirectionProperty.create("pointing");
    public static final BooleanProperty POINTED_N = BooleanProperty.create("pointed_n");
    public static final BooleanProperty POINTED_S = BooleanProperty.create("pointed_s");
    public static final BooleanProperty POINTED_W = BooleanProperty.create("pointed_w");
    public static final BooleanProperty POINTED_E = BooleanProperty.create("pointed_e");

    public static BooleanProperty get(Direction dir) {
        return switch (dir) {
            case NORTH -> POINTED_N;
            case SOUTH -> POINTED_S;
            case WEST -> POINTED_W;
            case EAST -> POINTED_E;
            default -> throw new IllegalStateException();
        };
    }

    public static List<Direction> getAll(BlockState state) {
        return Arrays.stream(Direction.values())
                .filter(dir -> dir.getAxis().isHorizontal())
                .filter(dir -> state.getValue(get(dir)))
                .toList();
    }

    public static final MapCodec<PackagerConnectorBlock> CODEC = simpleCodec(PackagerConnectorBlock::new);

    public PackagerConnectorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(POINTING, Direction.DOWN)
                .setValue(POINTED_N, false)
                .setValue(POINTED_S, false)
                .setValue(POINTED_W, false)
                .setValue(POINTED_E, false)
        );
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        IBE.onRemove(state, level, pos, newState);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> box(3,3,14, 13, 13, 16);
            case SOUTH -> box(3,3,0, 13, 13, 2);
            case WEST -> box(14,3,3, 16, 13, 13);
            case EAST -> box(0,3,3, 2, 13, 13);
            case UP -> box(3,0,3,13,2,13);
            case DOWN -> box(3,14,3,13,16,13);
        };
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {

        var level = context.getLevel();
        var pos = context.getClickedPos();

        Direction facing = state.getValue(FACING);
        Direction current = state.getValue(POINTING);
        List<Direction> currentPointed = getAll(state);

        List<Direction> cycle = new ArrayList<>();

        for(Direction dir : Direction.values()) {
            if(dir.getAxis().isVertical()) continue;
            if(!currentPointed.isEmpty() && currentPointed.contains(dir.getOpposite())) continue;
            Direction real = getRealDirectionOut(facing, dir);
            var stateAt = level.getBlockState(pos.relative(real));
            if(!stateAt.is(this)) continue;
            if(stateAt.getValue(FACING) != facing) continue;
            cycle.add(dir);
        }

        if(cycle.isEmpty())
            return InteractionResult.PASS;

        Direction result;
        if(current.getAxis().isVertical()) result = cycle.getFirst();
        else if(current == cycle.getLast()) result = Direction.DOWN;
        else result = cycle.get(cycle.indexOf(current)+1);

        IWrenchable.playRotateSound(level, pos);
        level.setBlock(pos, state.setValue(POINTING, result), 3);
        if(result.getAxis().isHorizontal()) {
            var pointedPos = pos.relative(getRealDirectionOut(facing, result));
            var pointed = level.getBlockState(pointedPos).setValue(get(result), true);
            level.setBlock(pointedPos, pointed, 3);
        }
        if(current.getAxis().isHorizontal()) {
            var pointedPos = pos.relative(getRealDirectionOut(facing, current));
            var pointed = level.getBlockState(pointedPos).setValue(get(current), false);
            level.setBlock(pointedPos, pointed, 3);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        var dir = state.getValue(FACING).getOpposite();
        return level.getBlockState(pos.relative(dir)).isFaceSturdy(level, pos, dir.getOpposite());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if(!canSurvive(state, level, pos))
            return Blocks.AIR.defaultBlockState();
        Direction facing = state.getValue(FACING);
        List<Direction> toRemove = getAll(state);
        toRemove = toRemove
                .stream()
                .filter(dir -> {
                    var pos1 = pos.relative(getRealDirectionIn(facing, dir));
                    var state1 = level.getBlockState(pos1);
                    return !state1.is(this);
                }).toList();

        Direction pointing = state.getValue(POINTING);

        if(!pointing.getAxis().isVertical()) {
            if(!level.getBlockState(pos.relative(getRealDirectionOut(facing, pointing))).is(this))
                state = state.setValue(POINTING, Direction.DOWN);
        }

        for(var dir : toRemove) {
            state = state.setValue(get(dir), false);
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    public Direction getRealDirectionOut(Direction panel, Direction connection) {
        if (panel.getAxis() == Direction.Axis.Y) {
            if(connection.getAxis() == Direction.Axis.Z && panel == Direction.UP) return connection;
            return connection.getOpposite();
        }

        if(connection == Direction.NORTH) return Direction.UP;
        if(connection == Direction.SOUTH) return Direction.DOWN;

        return connection == Direction.EAST ? panel.getClockWise() : panel.getCounterClockWise();
    }

    public Direction getRealDirectionIn(Direction panel, Direction connection) {
        if (panel.getAxis() == Direction.Axis.Y) {
            if(connection.getAxis() == Direction.Axis.Z && panel == Direction.UP) return connection.getOpposite();
            return connection;
        }

        if(connection == Direction.NORTH) return Direction.DOWN;
        if(connection == Direction.SOUTH) return Direction.UP;

        return connection == Direction.EAST ? panel.getCounterClockWise() : panel.getClockWise();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING, POINTING, POINTED_N, POINTED_S, POINTED_E, POINTED_W));
    }

    @Override
    public Class<PackagerConnectorBlockEntity> getBlockEntityClass() {
        return PackagerConnectorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PackagerConnectorBlockEntity> getBlockEntityType() {
        return RepackagedBlockEntityTypes.PACKAGER_CONNECTOR.get();
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }
}
