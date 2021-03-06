package gl.building;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import core.Resources;
import gl.Camera;
import gl.entity.GenericMeshShader;
import gl.res.Model;
import gl.res.Texture;
import map.Chunk;
import map.Terrain;
import scene.entity.EntityHandler;
import util.ModelBuilderOld;

public class BuildingRender {
	private static Texture materialTexture = null;
	
	public static int materialTextureScale = 32;
	public static float materialAtlasSize;
	
	private static Model selectorModel;
	private static Matrix4f selectorMatrix;
	
	private static BuildingShader shader;
	
	public static void loadAssets() {
		if (materialTexture != null) {
			materialTexture.delete();
		}
		materialTexture = Resources.addTexture("materials", "material/materials.png", GL11.GL_TEXTURE_2D, true, 32);
		materialAtlasSize = 1f / (materialTexture.size / materialTextureScale);
		
		shader = new BuildingShader();
		
		ModelBuilderOld builder = new ModelBuilderOld();
		builder.addVertex(0, 0, 0);
		builder.addVertex(1f, 0, 0);
		builder.addVertex(1f, 1f, 0);
		builder.addVertex(0, 1f, 0);
		builder.addTextureCoord(0, 0);
		builder.addTextureCoord(1, 0);
		builder.addTextureCoord(1, 1);
		builder.addTextureCoord(0, 1);
		builder.addNormal(1, 0, 0);
		builder.addNormal(1, 0, 0);
		builder.addNormal(1, 0, 0);
		builder.addNormal(1, 0, 0);
		builder.addIndices(0, 1, 3, 3, 1, 2, 2, 1, 3, 3, 1, 0);
		selectorModel = builder.finish();
		selectorMatrix = new Matrix4f();
	}
	
	public static void render(Camera camera, Vector3f lightDir, Vector3f selectionPoint, byte facing, Terrain terrain) {
		shader.start();
		shader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
		shader.lightDirection.loadVec3(lightDir);

		materialTexture.bind(0);
		for (int i = 0; i < Terrain.size; i++) {
			for (int j = 0; j < Terrain.size; j++) {
				Chunk chunk = terrain.get(i, j);
				if (chunk.getBuilding() == null) continue;
				final Model m = chunk.getBuilding().getModel();
				if (m != null) {
					m.bind(0, 1, 2, 3);
					m.getIndexVbo().bind();
					GL11.glDrawElements(GL11.GL_TRIANGLES, m.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
					m.unbind(0, 1, 2, 3);
				}
			}
		}
		
		shader.stop();
		
		if (selectionPoint != null) {
			GenericMeshShader meshShader = EntityHandler.getShader();
			meshShader.start();
			meshShader.projectionViewMatrix.loadMatrix(camera.getProjectionViewMatrix());
			meshShader.lightDirection.loadVec3(lightDir);

			selectorMatrix.identity();
			selectorMatrix.translate(selectionPoint);
			
			if (facing < 8) {
				if ((facing & 2) != 0) {
					selectorMatrix.rotateY(-90);
					selectorMatrix.translate(0f, 0f, -1f);
				}

				if ((facing & 4) != 0) {
					selectorMatrix.rotateX(90);
				}
			} else {
				switch (facing) {
				case 8:
					selectorMatrix.translate(0f, 0f, 1f);
					break;
				case 16:
					selectorMatrix.rotateY(180);
					selectorMatrix.translate(-1f, 0f, 0f);
					break;
				case 32:
					selectorMatrix.rotateY(-90);
					break;
				case 64:
					selectorMatrix.rotateY(90);
					selectorMatrix.translate(-1f, 0f, 1f);
					break;
				}

				selectorMatrix.rotateX(-45);
				
			}
			
			meshShader.modelMatrix.loadMatrix(selectorMatrix);
			
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		    GL11.glDisable(GL11.GL_TEXTURE_2D);
			final Model m = selectorModel;
			if (m != null) {
				m.bind(0, 1, 2);
				m.getIndexVbo().bind();
				GL11.glDrawElements(GL11.GL_TRIANGLES, m.getIndexCount(), GL11.GL_UNSIGNED_INT, 0);
				m.unbind(0, 1, 2);
			}
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL30.glBindVertexArray(0);

			meshShader.stop();
		}
		
	}
	
	public static void cleanUp() {
		selectorModel.cleanUp();
		shader.cleanUp();
	}
}
