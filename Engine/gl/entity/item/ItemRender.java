package gl.entity.item;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import core.Resources;
import gl.Camera;
import gl.building.BuildingRender;
import gl.res.Texture;
import scene.PlayableScene;
import scene.entity.Entity;
import scene.entity.utility.ItemEntity;
import scene.overworld.inventory.Inventory;
import scene.overworld.inventory.Item;
import scene.overworld.inventory.ItemData;

public class ItemRender {
	
	private ItemShader shader;
	private Texture texture;

	public ItemRender() {
		shader = new ItemShader();
		texture = null;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public void render(PlayableScene scene, Camera camera, Vector3f lightDirection, List<Entity> list) {
		if (texture == null) return;
		
		shader.start();
		
		shader.lightDirection.loadVec3(lightDirection);
		shader.projectionMatrix.loadMatrix(camera.getProjectionMatrix());
		shader.diffuse.loadTexUnit(0);
		
		texture.bind(0);
		Resources.QUAD2D.bind(0, 1);

		final float size = Inventory.itemAtlasSize;

		for(Entity entity : list) {
			if (entity.deactivated) continue;
			entity.update(scene);
			shader.viewMatrix.loadMatrix(buildViewMatrix(entity.position, camera.getViewMatrix()));
			ItemData item = Item.get(((ItemEntity) entity).getItem());
			
			if (item.isUsingMaterialTexture()) {
				Resources.getTexture("materials").bind(0);
				shader.uv.loadVec3(item.getTX(), item.getTY(), BuildingRender.materialAtlasSize);
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
				texture.bind(0);
			} else {
				shader.uv.loadVec3(item.getTX(), item.getTY(), size);
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
			}
		}

		Resources.QUAD2D.unbind(0, 1);
		texture.unbind();
		shader.stop();
	}

	private Matrix4f buildViewMatrix(Vector3f position, Matrix4f viewMatrix) {
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.translate(position.x, position.y + .4f, position.z);
		modelMatrix.m00 = viewMatrix.m00;
	    modelMatrix.m01 = viewMatrix.m10;
	    modelMatrix.m02 = viewMatrix.m20;
	    modelMatrix.m10 = viewMatrix.m01;
	    modelMatrix.m11 = viewMatrix.m11;
	    modelMatrix.m12 = viewMatrix.m21;
	    modelMatrix.m20 = viewMatrix.m02;
	    modelMatrix.m21 = viewMatrix.m12;
	    modelMatrix.m22 = viewMatrix.m22;
	    //modelMatrix.rotate((float)Math.toRadians(rot), new Vector3f(0,0,1));
	    modelMatrix.scale(.4f);
	    Matrix4f mvMatrix = new Matrix4f();
	    Matrix4f.mul(viewMatrix, modelMatrix, mvMatrix);
	    return mvMatrix;
	}	
	
	public void cleanUp() {
		shader.cleanUp();
	}

	public Texture getTexture() {
		return texture;
	}
}
