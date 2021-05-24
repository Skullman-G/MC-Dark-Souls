Stream.of(
Block.makeCuboidShape(2, 0.75, 2, 14, 1.75, 14),
Block.makeCuboidShape(1, 0, 1, 15, 1, 15),
Block.makeCuboidShape(4, 2, 4, 12, 3, 12),
Block.makeCuboidShape(6, 3, 6, 10, 4, 10),
Block.makeCuboidShape(7, 4, 8, 8, 20, 9),
Block.makeCuboidShape(6, 15, 8, 7, 16, 9),
Block.makeCuboidShape(8, 15, 8, 9, 16, 9)
).reduce((v1, v2) -> {return VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR);});