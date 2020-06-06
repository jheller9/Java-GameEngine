package gl.particle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import core.Globals;
import core.Resources;
import core.res.Texture;
import gl.Camera;

public class ParticleHandler {
	private static Map<Texture, List<Particle>> particles = Collections
			.synchronizedMap(new LinkedHashMap<Texture, List<Particle>>());
	private static ParticleRenderer renderer;
	private static int particleCount = 0;

	public static void add(Particle p) {
		if (particleCount > Globals.maxParticles + 1) {
			// particles.remove(entry.getKey(),entry.getValue());
			// particleCount--;
			return;
		}
		List<Particle> list = particles.get(p.getTexture());

		if (list == null) {
			list = Collections.synchronizedList(new ArrayList<Particle>());
			particles.put(p.getTexture(), list);
			// list.add(p);
		}
		/*
		 * else if (numAlive < list.size()) { for(Particle particle : list) { if
		 * (!particle.isAlive()) { particle.setActive(p.getPosition(), p.getVelocity(),
		 * p.getGravity(), p.getLife(), p.getRotation(), p.getScale()); } } } else {
		 */
		list.add(p);
		particleCount++;
		// }
	}

	public static void cleanUp() {
		renderer.cleanup();
	}

	public static void init() {
		renderer = new ParticleRenderer();
		Resources.addTexture("particles", "particles/particles.png", GL11.GL_TEXTURE_2D, true, 8);
		Resources.addTexture("smoke", "particles/smoke.png", GL11.GL_TEXTURE_2D, true, 8);
	}

	public static void render(Camera camera) {
		renderer.render(particles, camera);
	}

	private static void sortParticles(List<Particle> list) {
		for (int i = 1; i < list.size(); i++) {
			final Particle item = list.get(i);
			if (item.getDistance() > list.get(i - 1).getDistance()) {
				sortUpHighToLow(list, i);
			}
		}
	}

	private static void sortUpHighToLow(List<Particle> list, int i) {
		final Particle item = list.get(i);
		int attemptPos = i - 1;
		while (attemptPos != 0 && list.get(attemptPos - 1).getDistance() < item.getDistance()) {
			attemptPos--;
		}
		list.remove(i);
		list.add(attemptPos, item);
	}

	public static void update(Camera camera) {
		final Iterator<Entry<Texture, List<Particle>>> mapIterator = particles.entrySet().iterator();
		while (mapIterator.hasNext()) {
			final Entry<Texture, List<Particle>> entry = mapIterator.next();
			final List<Particle> list = entry.getValue();

			synchronized (list) {
				final Iterator<Particle> iter = list.iterator();
				while (iter.hasNext()) {
					final Particle p = iter.next();
					if (p.isAlive()) {
						final boolean alive = p.update(camera);
						if (!alive) {
							iter.remove();
							particleCount--;
						}
					}
				}
			}

			// if (!entry.getKey().isAdditive()) {
			sortParticles(list);
			// }
		}
	}

	public static void addBurst(Texture texture, int start, int end, Vector3f position) {
		for(float i = 0; i < .36f; i += .12f) {
			for(float j = 0; j < .36f; j += .12f) {
				for(float k = 0; k < .36f; k += .12f) {
					float dx = -(.17f - i) / 18f;
					float dy = .01f;
					float dz = -(.17f - k) / 18f;
					Particle p = new Particle(texture, new Vector3f(position.x+i, position.y+j+.25f, position.z+k), new Vector3f(dx,dy, dz), .0028f, 50, 1, 1, .15f);
					p.setTextureAtlasRange(start, end);

				}
			}
		}
	}

	public static int size() {
		return particleCount;
	}
}
