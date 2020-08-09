# CurvatureEnhancedMeshSimplification
A Java implementation of the algorithm described in "Mesh Simplification by Curvature-Enhanced Quadratic ErrorMetrics"

The method presented is an iterative edge contraction algorithm based on the work of Garland and Heckberts. The original algorithm is improved by enhancing the quadratic error metrics with a penalizing factor based on discrete gaussian curvature, which is estimated efficiently through the Gauss-Bonnet theorem, to account for the presence of fine details during the edge decimation process.
