package org.dyn4j.fhnw;

import org.dyn4j.collision.narrowphase.Penetration;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.CollisionAdapter;
import org.dyn4j.dynamics.World;

public class ArenaContactListener extends CollisionAdapter {
	private Body wall;
	private Robot robot;
	private World world;
	
	public ArenaContactListener(Robot robot, Body wall, World w) {
		this.robot = robot;
		this.wall = wall;
		this.world = w;
	}
	@Override
	public boolean collision(Body body1, BodyFixture fixture1, Body body2, BodyFixture fixture2, Penetration penetration) {
		// the bodies can appear in either order
		if ((body1 == robot && body2 == wall) ||
			    (body1 == wall && body2 == robot)) {
			// its the collision we were looking for
			// do whatever you need to do here
			
			// stopping them like this isn't really recommended
			// there are probably better ways to do what you want
			
			//System.out.println("HITTTED, body going to be removed: " +robot.getRobotNr());
			robot.setActive(false);
			
			
			body1.getLinearVelocity().zero();
			body1.setAngularVelocity(0.0);
			body2.getLinearVelocity().zero();
			body2.setAngularVelocity(0.0);
			world.removeBody(robot);
			return false;
		}
		return true;
	}
	
	
}
