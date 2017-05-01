package com.jdub.generals;

import pl.joegreen.sergeants.framework.model.*;

import java.util.*;

/**
 * Created by jameswarren on 4/26/17.
 *
 *
 * PathFinder is a simplified version of Dijkstra's algorithm.  It is used to calculated paths to enemies and generals.
 *
 * Since we calculate the paths on each update, the methods return the neighbor in which we are going to move to
 * by working back from the destination position, to the initial position's neighbor.
 *
 * This in turn will allow the Bot to adjust its intended path on each update.  It's mo' betta!
 *
 */
public class PathFinder {
    private PriorityQueue<PathNode> queue;
    private Map<Position, PathNode> pathNodes = new HashMap<Position, PathNode>();

    private GameState gameState;
    private PathNode source;

    public PathFinder(GameState gameState, Field source) {

        this.gameState = gameState;
        this.source = new PathNode(source);

        //  using a priority queue gets a better way to choose the right neighbor to select next
        this.queue = new PriorityQueue<PathNode>(10, new PathNodeComparator(this.source));
        loadGameNodes();
        calculateDistances();
    }

    private void loadGameNodes() {
        for (Map.Entry<Position, Field> entry : gameState.getFieldsMap().entrySet()) {
            this.pathNodes.put(entry.getKey(), new PathNode(entry.getValue()));
        }
    }

    private void calculateDistances() {

        /*
         * Simplified version of Dijkstra's algorithm, all the weights are equal so we only have to increment
         * the neighbors by 1, and we can walk the parent back to the source for shortest path.
         *
         * I could see this being modified to take into account army size for shortest path with the greatest
         * army value, as a possibility.
        */

        this.source.setPathDistance(0);
        this.queue.offer(this.source);

        while (this.queue.size() != 0) {
            PathNode vertex = this.queue.poll();
            addDistanceToAdjacentNodes(vertex);
        }
    }

    private void addDistanceToAdjacentNodes(PathNode vertex) {
        Field workingField = vertex.getField();
        for (Field neighborField : workingField.getNeighbours()) {
            PathNode neighborNode = this.pathNodes.get(neighborField.getPosition());
            if (neighborNode.isSettled()) {
                continue;
            }

            neighborNode.setSettled(true);
            if (neighborField.isVisible() && neighborField.asVisibleField().isCity()) {
                continue;
            }

            if (neighborField.isVisible() && neighborField.isObstacle())
                continue;;

            neighborNode.setParent(vertex);
            neighborNode.setPathDistance(vertex.getPathDistance() + 1);
            this.queue.offer(neighborNode);

        }
    }

    private PathNode findPathToSourceFirstStep(PathNode targetNode) {
        while (targetNode.getPathDistance() + 1 > this.source.getPathDistance()) {
            if (targetNode.getParent().equals(this.source)) {
                return targetNode;
            }
            targetNode = targetNode.getParent();
        }
        return targetNode;
    }

    private boolean isBorderNode(Field field) {
        if (!field.isVisible())
            return false;

        VisibleField visibleField = field.asVisibleField();
        if (!visibleField.isBlank())
            return false;

        return visibleField.getVisibleNeighbours().stream().anyMatch(
                neighbor -> neighbor.isOwnedByMe()
        );
    }

    private Field findTargetsFirstStepByField(Optional<Field> targetField) {
        if (!targetField.isPresent())
            return null;

        Field borderField = targetField.get();

        PathNode pathNode = pathNodes.get(borderField.getPosition());
        PathNode stepNode = findPathToSourceFirstStep(pathNode);
        if (stepNode == null)
            return null;

        return stepNode.getField();
    }

    private Field findTargetsFirstStepByPathNode(Optional<PathNode> targetField) {
        if (!targetField.isPresent())
            return null;

        PathNode pathNode = targetField.get();
        PathNode stepNode = findPathToSourceFirstStep(pathNode);
        if (stepNode == null)
            return null;

        return stepNode.getField();
    }

    public Field getRandomNeighborToEmptyField() {
        // filter on ANY border node
        Optional<Field> borderNode = gameState.getFields().stream()
                .filter(this::isBorderNode)
                .findAny();

        return findTargetsFirstStepByField(borderNode);
    }

    public Field getNearestEnemy() {
        Optional<PathNode> nearestEnemy = pathNodes.values().stream()
                .filter(PathNode::isEnemy)
                .sorted(Comparator.comparing(PathNode::getPathDistance))
                .findFirst();

        return findTargetsFirstStepByPathNode(nearestEnemy);
    }

    public Field getNearestGeneral() {
        Optional<PathNode> nearestGeneral = pathNodes.values().stream()
                .filter(PathNode::isGeneral)
                .sorted(Comparator.comparing(PathNode::getPathDistance))
                .findFirst();

        return findTargetsFirstStepByPathNode(nearestGeneral);
    }

}
