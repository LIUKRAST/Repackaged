package net.liukrast.repackaged.content.logistics;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.NonnullDefault;

import java.util.Locale;

@NonnullDefault
public class PackageShelfBlock extends BaseEntityBlock implements IWrenchable {
    public static final MapCodec<PackageShelfBlock> CODEC = simpleCodec(PackageShelfBlock::new);
    private static final VoxelShape RODS = Shapes.or(
            box(0,0,0,2,16,2),
            box(14,0,14,16, 16, 16),
            box(14,0,0,16,16,2),
            box(0,0,14,2, 16, 16)
    );
    private static final VoxelShape SINGLE_SHAPE = box(0,0,0,16,11,16);
    private static final VoxelShape BOTTOM_SHAPE = Shapes.or(
            SINGLE_SHAPE,
            RODS
    );
    private static final VoxelShape TOP_SHAPE = Shapes.or(
            box(0, 10, 0, 16, 16, 16),
            RODS
    );
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create("shape", Shape.class);
    public static final DirectionProperty FACING = DirectionProperty.create("facing", dir -> dir != Direction.DOWN);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;


    public PackageShelfBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(SHAPE, Shape.SINGLE)
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(SHAPE)) {
            case BOTTOM -> BOTTOM_SHAPE;
            case TOP -> TOP_SHAPE;
            case MIDDLE -> RODS;
            case SINGLE -> SINGLE_SHAPE;
        };
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState toReturn = super.updateShape(state, facing, neighborState, level, pos, neighborPos);
        if(facing.getAxis().isHorizontal())
            return toReturn;
        var upState = level.getBlockState(pos.above());
        boolean up = upState.is(this) && upState.getValue(SHAPE).checkTop();
        var downState = level.getBlockState(pos.below());
        boolean down = downState.is(this) && downState.getValue(SHAPE).checkBottom();
        return state.setValue(SHAPE, down && up ? Shape.MIDDLE : down ? Shape.TOP : up ? Shape.BOTTOM : Shape.SINGLE); 
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHAPE, POWERED, FACING);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        var player = context.getPlayer();
        if(player != null && player.isShiftKeyDown()) return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection());
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var belowState = level.getBlockState(pos.below());
        var aboveState = level.getBlockState(pos.above());
        if(belowState.is(this)) return belowState
                .setValue(SHAPE, aboveState.is(this) ? Shape.MIDDLE : Shape.TOP)
                .setValue(POWERED, false);
        if(aboveState.is(this)) return aboveState.setValue(SHAPE, Shape.BOTTOM).setValue(POWERED, false);
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        super.onNeighborChange(state, level, pos, neighbor);
        notifySource(level, pos);
    }

    public void notifySource(LevelReader level, BlockPos pos) {
        while(true) {
            var state = level.getBlockState(pos.above());
            if(!state.is(this)) break;
            pos = pos.above();
        }
        int counter = 0;
        while(true) {
            var state = level.getBlockState(pos);
            if(!state.is(this)) return;
            counter++;
            if(state.getValue(SHAPE) == Shape.BOTTOM) {
                //TODO: Notify blockentity
                System.out.println("Found " + counter + " blocks");
                return;
            }
            pos = pos.below();
        }
    }


    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }


    public enum Shape implements StringRepresentable {
        SINGLE,
        BOTTOM,
        MIDDLE,
        TOP;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public boolean checkTop() {
            return this == TOP || this == MIDDLE;
        }

        public boolean checkBottom() {
            return this == BOTTOM || this == MIDDLE;
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
