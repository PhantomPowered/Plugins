package com.github.phantompowered.plugins.pathfinding.walk;

import com.github.phantompowered.proxy.api.block.material.Material;
import com.github.phantompowered.proxy.api.connection.ServiceConnection;
import com.github.phantompowered.proxy.api.location.Location;
import com.github.phantompowered.plugins.pathfinding.Path;
import com.github.phantompowered.plugins.pathfinding.PathPoint;

import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class FollowingWalkablePath extends WalkablePath {

    private final Path path;

    private PathPoint previousPoint;
    private PathPoint currentPoint;
    private Queue<PathPoint> currentWay;

    public FollowingWalkablePath(ServiceConnection connection, Path path, Runnable finishHandler) {
        super(connection, finishHandler);
        this.path = path;
    }

    public Path getPath() {
        return this.path;
    }

    @Override
    public boolean isRecursive() {
        return this.path.isRecursive();
    }

    @Override
    public boolean isDone() {
        return !this.path.hasPointsLeft() && (this.currentWay == null || this.currentWay.isEmpty());
    }

    public PathPoint getCurrentPoint() {
        return this.currentPoint;
    }

    public void setCurrentPoint(PathPoint currentPoint) {
        this.previousPoint = this.currentPoint;
        this.currentPoint = currentPoint;
    }

    public PathPoint getPreviousPoint() {
        return this.previousPoint;
    }

    public void setPreviousPoint(PathPoint previousPoint) {
        this.previousPoint = previousPoint;
    }

    public Queue<PathPoint> getCurrentWay() {
        return this.currentWay;
    }

    public void setCurrentWay(Queue<PathPoint> currentWay) {
        this.currentWay = currentWay;
    }

    @Override
    public void handleTick() {
        PathPoint point = this.getCurrentWay() == null ? null : this.getCurrentWay().poll();

        if (point == null) {
            PathPoint previousPoint = this.getCurrentPoint();
            PathPoint currentPoint = this.path.getNextPoint();
            this.setCurrentPoint(currentPoint);
            this.setCurrentWay(this.smoothWay(previousPoint, currentPoint));
            return;
        }

        Location location = this.getPath().getAbsoluteLocation(point);
        PathPoint nextPoint = this.getCurrentPoint();
        if (nextPoint != null) {
            location.setDirection(this.getPath().getAbsoluteLocation(nextPoint).subtract(this.getConnection().getLocation()).toVector());
        }

        this.getConnection().teleport(location);
    }

    private Queue<PathPoint> smoothWay(PathPoint previousPoint, PathPoint currentPoint) {
        // TODO This doesn't seem to be working well on diagonal paths

        if (currentPoint == null) {
            return null;
        }
        if (previousPoint == null) {
            return new LinkedBlockingQueue<>(Collections.singletonList(currentPoint));
        }

        double deltaX = (currentPoint.getX() - previousPoint.getX());
        double deltaY = (currentPoint.getY() - previousPoint.getY());
        double deltaZ = (currentPoint.getZ() - previousPoint.getZ());

        if (deltaY > 0.6 && (deltaX > 0.5 || deltaZ > 0.5)) {
            PathPoint midPoint = new PathPoint(previousPoint.getX(), currentPoint.getY(), previousPoint.getZ());
            Queue<PathPoint> result = new LinkedBlockingQueue<>();
            result.addAll(this.smoothWay(previousPoint, midPoint));
            result.addAll(this.smoothWay(midPoint, currentPoint));
            return result;
        }

        return this.smoothWay0(previousPoint, currentPoint);
    }

    private Queue<PathPoint> smoothWay0(PathPoint previousPoint, PathPoint currentPoint) {
        // TODO you can fall off corners easily
        double pointCount = DefaultPathWalker.BPT;

        double deltaX = Math.abs(currentPoint.getX() - previousPoint.getX());
        double deltaY = Math.abs(currentPoint.getY() - previousPoint.getY());
        double deltaZ = Math.abs(currentPoint.getZ() - previousPoint.getZ());

        double stepX = pointCount;// / deltaX;
        double stepY = pointCount;// / deltaY;
        double stepZ = pointCount;// / deltaZ;

        Queue<PathPoint> result = new LinkedBlockingQueue<>();

        double x = 0;
        double y = 0;
        double z = 0;
        while (x < deltaX || y < deltaY || z < deltaZ) {
            if (x < deltaX) {
                x += stepX;
            }
            if (y < deltaY) {
                y += stepY;
            }
            if (z < deltaZ) {
                z += stepZ;
            }

            result.offer(new PathPoint(previousPoint.getX() + x, previousPoint.getY() + y, previousPoint.getZ() + z));
        }

        return result;
    }

}
