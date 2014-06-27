package net.cogzmc.core.util;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

@Data
@RequiredArgsConstructor(staticName = "of")
public final class Point {
    @NonNull private final Double x;
    @NonNull private final Double y;
    @NonNull private final Double z;
    @NonNull private final Float pitch;
    @NonNull private final Float yaw;

    public Location getLocation(World world) {
        return new Location(world, x, y, z, pitch, yaw);
    }

    public static Point of(Double x, Double y, Double z) {
        return Point.of(x, y, z, 0f, 0f);
    }

    public static Point of(Location location) {
        return Point.of(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }

    public static Point of(Block block) {
        return Point.of(block.getLocation());
    }

    public Double distanceSquared(Point point) {
        Double x = (this.x-point.x)*(this.x-point.x);
        Double y = (this.y-point.y)*(this.y-point.y);
        Double z = (this.z-point.z)*(this.z-point.z);
        return x + y + z;
    }

    public Double distance(Point point) {
        return Math.sqrt(distanceSquared(point));
    }
}
