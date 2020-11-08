# CurvatureEnhancedMeshSimplification
A Java implementation of the algorithm described in "Mesh Simplification by Curvature-Enhanced Quadratic ErrorMetrics"

The method presented is an iterative edge contraction algorithm based on the work of Garland and Heckberts. The original algorithm is improved by enhancing the quadratic error metrics with a penalizing factor based on discrete gaussian curvature, which is estimated efficiently through the Gauss-Bonnet theorem, to account for the presence of fine details during the edge decimation process.

### Reference:
Please cite this work as:
Pellizzoni, P. & Savio, G. (2020). Mesh Simplification by Curvature-Enhanced Quadratic Error Metrics. Journal of Computer Science, 16(8), 1195-1202. https://doi.org/10.3844/jcssp.2020.1195.1202
