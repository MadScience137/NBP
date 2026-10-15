package ch.nbodyproblems.rendering.material;

import ch.nbodyproblems.rendering.Color;
import ch.nbodyproblems.rendering.material.property.number.FloatMaterialProperty;
import ch.nbodyproblems.rendering.material.property.vector.ColorMaterialProperty;
import ch.nbodyproblems.rendering.material.property.vector.Vector3fMaterialProperty;
import org.joml.Vector3f;

public class PbrMaterial extends Material {
    private final ColorMaterialProperty ambientLightColor = new ColorMaterialProperty("ambientLightColor");
    private final ColorMaterialProperty lightColor = new ColorMaterialProperty("lightColor");
    private final Vector3fMaterialProperty lightDirection = new Vector3fMaterialProperty(new Vector3f(0.75f, 0.5f, 1).normalize(), "lightDirection");
    private final ColorMaterialProperty albedo = new ColorMaterialProperty("albedo");
    private final FloatMaterialProperty roughness = new FloatMaterialProperty(0.5f, "roughness");
    private final FloatMaterialProperty metallic = new FloatMaterialProperty(0.5f, "metallic");
    private final FloatMaterialProperty ambientOcclusion = new FloatMaterialProperty(0f, "ao");

    private static final String VERTEX_SHADER_PATH = "/shaders/pbr.vert";
    private static final String FRAGMENT_SHADER_PATH = "/shaders/pbr.frag";

    public PbrMaterial(Color albedo) {
        super(VERTEX_SHADER_PATH, FRAGMENT_SHADER_PATH);
        setAlbedo(albedo);
        setLightColor(Color.WHITE);
        setAmbientLightColor(new Color(0.1f, 0.1f, 0.1f));
    }

    public void setAlbedo(Color albedo) {
        this.albedo.set(albedo);
    }

    public void setLightColor(Color color) {
        this.lightColor.set(color);
    }

    public void setAmbientLightColor(Color color) {
        this.ambientLightColor.set(color);
    }
}
