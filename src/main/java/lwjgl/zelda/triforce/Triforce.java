package lwjgl.zelda.triforce;

import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class Triforce {

    private static final float[] LIGHT_POSITION = {0.0f, 0.0f, 10.0f, 1.0f};
    private static final float[] WHITE_LIGHT = {1f, 1f, 1f, 1f};
    private static final float[] TRIANGLE_COLOR = {0.86f, 0.74f, 0.14f};
    private static final float[] SIDE_COLOR = {0.78f, 0.59f, 0.0f};

    private final FloatBuffer lightPosBuffer = toFloatBuffer(LIGHT_POSITION);
    private final FloatBuffer whiteLightBuffer = toFloatBuffer(WHITE_LIGHT);

    private double angle = 0;
    private static final int DEPTH = -30;

    public void render() {
        setupProjection();
        setupModelView();
        clearScreen();
        setupLighting();

        drawTriangle(-5, -5, DEPTH);
        drawTriangle(5, -5, DEPTH);
        drawTriangle(0, 5, DEPTH);

        angle = (angle + 1.0) % 360.0;
    }

    private void setupProjection() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();

        float aspect = 800f / 600f;
        float fov = 60f;
        float near = 0.1f;
        float far = 100f;
        float top = (float) Math.tan(Math.toRadians(fov / 2)) * near;
        float bottom = -top;
        float right = top * aspect;
        float left = -right;

        GL11.glFrustum(left, right, bottom, top, near, far);
    }

    private void setupModelView() {
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    private void clearScreen() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private void setupLighting() {
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);

        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE);

        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPosBuffer);
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, whiteLightBuffer);
    }

    private void drawTriangle(float tx, float ty, float tz) {
        GL11.glPushMatrix();
        GL11.glTranslatef(tx, ty, tz);
        GL11.glRotated(angle, 0.0f, 1.0f, 0.0f);

        // Front face
        GL11.glBegin(GL11.GL_TRIANGLES);
        setColor(TRIANGLE_COLOR);
        GL11.glNormal3f(0, 0, 1);
        GL11.glVertex3f(-5, -5, 1);
        GL11.glVertex3f(0, 5, 1);
        GL11.glVertex3f(5, -5, 1);
        GL11.glEnd();

        // Back face
        GL11.glBegin(GL11.GL_TRIANGLES);
        setColor(TRIANGLE_COLOR);
        GL11.glNormal3f(0, 0, -1);
        GL11.glVertex3f(-5, -5, -1);
        GL11.glVertex3f(0, 5, -1);
        GL11.glVertex3f(5, -5, -1);
        GL11.glEnd();

        // Side faces
        GL11.glBegin(GL11.GL_QUADS);
        setColor(SIDE_COLOR);

        // Left side
        GL11.glNormal3f(-1, 0, 0);
        GL11.glVertex3f(-5, -5, -1);
        GL11.glVertex3f(0, 5, -1);
        GL11.glVertex3f(0, 5, 1);
        GL11.glVertex3f(-5, -5, 1);

        // Right side
        GL11.glNormal3f(1, 0, 0);
        GL11.glVertex3f(5, -5, 1);
        GL11.glVertex3f(0, 5, 1);
        GL11.glVertex3f(0, 5, -1);
        GL11.glVertex3f(5, -5, -1);
        GL11.glEnd();

        GL11.glPopMatrix();
    }

    private void setColor(float[] color) {
        GL11.glColor3f(color[0], color[1], color[2]);
    }

    private FloatBuffer toFloatBuffer(float[] values) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
        buffer.put(values).flip();
        return buffer;
    }
}
