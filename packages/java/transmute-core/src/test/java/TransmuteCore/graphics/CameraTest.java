package TransmuteCore.graphics;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CameraTest
{
    @Test
    public void lookAtCentersAndClampWorks()
    {
        Camera cam = new Camera(100, 80);
        cam.lookAt(50, 40);
        assertEquals(0f, cam.getX(), 0.01f);
        assertEquals(0f, cam.getY(), 0.01f);

        cam.lookAt(200, 200);
        cam.clampToWorld(300, 240);
        assertEquals(150f, cam.getX(), 0.01f);
        assertEquals(160f, cam.getY(), 0.01f);
        assertEquals(50, cam.worldToScreenX(200));
        assertEquals(40, cam.worldToScreenY(200));
    }
}
