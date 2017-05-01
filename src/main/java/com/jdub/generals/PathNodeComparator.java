package com.jdub.generals;

/**
 * Created by jameswarren on 4/26/17.
 */
public class PathNodeComparator implements java.util.Comparator<PathNode> {
    private PathNode source;

    public PathNodeComparator(PathNode source) {
        this.source = source;
    }

    @Override
    public int compare(PathNode o1, PathNode o2) {
        if (o1.getPathDistance().intValue() < o2.getPathDistance().intValue())
            return -1;
        if (o1.getPathDistance().intValue() > o2.getPathDistance().intValue())
            return 1;
        // if they are equal distance from source, calculate their distance from target
        // close to the target first
        if (calculateDistanceToTarget(o1) < calculateDistanceToTarget(o2))
            return -1;
        if (calculateDistanceToTarget(o1) > calculateDistanceToTarget(o2))
            return 1;

        return 0;
    }

    private double calculateDistanceToTarget(PathNode pathNode) {
        return Math.sqrt(
                ((source.getField().getPosition().getCol() - pathNode.getField().getPosition().getCol()) ^ 2) +
                        ((source.getField().getPosition().getRow() - pathNode.getField().getPosition().getRow()) ^ 2)
        );
    }
}
