import java.util.Arrays;

public class Cube {
    private static float A = 0, B = 0, C = 0; // Euler angles for rotation
    private static float cubeWidth = 20; // Initial width of first cube
    private static final int SCREEN_WIDTH = 140, SCREEN_HEIGHT = 40; // Screen dimensions
    private static float[] zBuffer = new float[140 * 40]; // Z-buffer for hidden surface removal
    private static char[] buffer = new char[140 * 40]; // Buffer for rendering ASCII art
    private static final char BACKGROUND_CHAR = '\'';
    private static final int DISTANCE_FROM_CAMERA = 120; // Distance of the camera from the cube
    private static final float PERSPECTIVE_CONSTANT = 40f; // Constant for perspective projection
    private static float incrementSpeed = (float) 0.6; // Speed of increment for cube position
    static float horizontalOffset; // Horizontal offset for multi-cube rendering

    // For each cube point (i, j, k), calculate its coordinates (x, y, z) in the
    // rotated space using Euler angles A, B, and C

    // x = j * sin(A) * sin(B) * cos(C) - k * cos(A) * sin(B) * cos(C) + j * cos(A)
    // * sin(C) + k * sin(A) * sin(C) + i * cos(B) * cos(C)
    // y = j * cos(A) * cos(C) + k * sin(A) * cos(C) - j * sin(A) * sin(B) * sin(C)
    // + k * cos(A) * sin(B) * sin(C) - i * cos(B) * sin(C)
    // z = k * cos(A) * cos(B) - j * sin(A) * cos(B) + i * sin(B)
    static float[] calculateXYZ(int i, int j, int k) {
        float[] xyz = new float[3];
        xyz[0] = j * (float) Math.sin(A) * (float) Math.sin(B) * (float) Math.cos(C)
                - k * (float) Math.cos(A) * (float) Math.sin(B) * (float) Math.cos(C)
                + j * (float) Math.cos(A) * (float) Math.sin(C)
                + k * (float) Math.sin(A) * (float) Math.sin(C)
                + i * (float) Math.cos(B) * (float) Math.cos(C);
        xyz[1] = j * (float) Math.cos(A) * (float) Math.cos(C)
                + k * (float) Math.sin(A) * (float) Math.cos(C)
                - j * (float) Math.sin(A) * (float) Math.sin(B) * (float) Math.sin(C)
                + k * (float) Math.cos(A) * (float) Math.sin(B) * (float) Math.sin(C)
                - i * (float) Math.cos(B) * (float) Math.sin(C);
        xyz[2] = k * (float) Math.cos(A) * (float) Math.cos(B)
                - j * (float) Math.sin(A) * (float) Math.cos(B)
                + i * (float) Math.sin(B);
        return xyz;
    }

    // Calculate the screen coordinates (xp, yp) of a cube surface point with
    // coordinates (cubeX, cubeY, cubeZ) projected onto the screen

    // The z-coordinate of the point is modified to adjust for perspective
    // projection

    // The visibility of the point is checked using a z-buffer for hidden surface
    // removal

    // xp = SCREEN_WIDTH / 2 + horizontalOffset + PERSPECTIVE_CONSTANT * (1 / z) * x
    // * 2
    // yp = SCREEN_HEIGHT / 2 + PERSPECTIVE_CONSTANT * (1 / z) * y
    static void calculateForSurface(float cubeX, float cubeY, float cubeZ, char ch) {
        float[] xyz = calculateXYZ((int) cubeX, (int) cubeY, (int) cubeZ);
        float x = xyz[0];
        float y = xyz[1];
        float z = xyz[2] + DISTANCE_FROM_CAMERA;

        float ooz = 1 / z;

        int xp = (int) (SCREEN_WIDTH / 2 + horizontalOffset + PERSPECTIVE_CONSTANT * ooz * x * 2);
        int yp = (int) (SCREEN_HEIGHT / 2 + PERSPECTIVE_CONSTANT * ooz * y);

        int idx = xp + yp * SCREEN_WIDTH;
        if (idx >= 0 && idx < SCREEN_WIDTH * SCREEN_HEIGHT) {
            if (ooz > zBuffer[idx]) {
                zBuffer[idx] = ooz;
                buffer[idx] = ch;
            }
        }
    }

    public static void main(String[] args) {
        // Hide cursor
        System.out.print("\033[?25l");

        // Add shutdown hook to restore cursor
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Show cursor
            System.out.print("\033[?25h");
        }));

        // https://stackoverflow.com/a/39764876
        System.out.print("\033[H");
        while (true) {
            Arrays.fill(buffer, BACKGROUND_CHAR);
            Arrays.fill(zBuffer, 0);
            cubeWidth = 20;
            horizontalOffset = -2 * cubeWidth;

            for (float cubeX = -cubeWidth; cubeX < cubeWidth; cubeX += incrementSpeed) {
                for (float cubeY = -cubeWidth; cubeY < cubeWidth; cubeY += incrementSpeed) {
                    calculateForSurface(cubeX, cubeY, -cubeWidth, '@');
                    calculateForSurface(cubeWidth, cubeY, cubeX, '$');
                    calculateForSurface(-cubeWidth, cubeY, -cubeX, '~');
                    calculateForSurface(-cubeX, cubeY, cubeWidth, '#');
                    calculateForSurface(cubeX, -cubeWidth, -cubeY, ';');
                    calculateForSurface(cubeX, cubeWidth, cubeY, '+');
                }
            }
            cubeWidth = 10;
            horizontalOffset = 1 * cubeWidth;
            for (float cubeX = -cubeWidth; cubeX < cubeWidth; cubeX += incrementSpeed) {
                for (float cubeY = -cubeWidth; cubeY < cubeWidth; cubeY += incrementSpeed) {
                    calculateForSurface(cubeX, cubeY, -cubeWidth, '@');
                    calculateForSurface(cubeWidth, cubeY, cubeX, '$');
                    calculateForSurface(-cubeWidth, cubeY, -cubeX, '~');
                    calculateForSurface(-cubeX, cubeY, cubeWidth, '#');
                    calculateForSurface(cubeX, -cubeWidth, -cubeY, ';');
                    calculateForSurface(cubeX, cubeWidth, cubeY, '+');
                }
            }
            cubeWidth = 5;
            horizontalOffset = 8 * cubeWidth;
            for (float cubeX = -cubeWidth; cubeX < cubeWidth; cubeX += incrementSpeed) {
                for (float cubeY = -cubeWidth; cubeY < cubeWidth; cubeY += incrementSpeed) {
                    calculateForSurface(cubeX, cubeY, -cubeWidth, '@');
                    calculateForSurface(cubeWidth, cubeY, cubeX, '$');
                    calculateForSurface(-cubeWidth, cubeY, -cubeX, '~');
                    calculateForSurface(-cubeX, cubeY, cubeWidth, '#');
                    calculateForSurface(cubeX, -cubeWidth, -cubeY, ';');
                    calculateForSurface(cubeX, cubeWidth, cubeY, '+');
                }
            }
            System.out.print("\033[H");
            for (int k = 0; k < SCREEN_WIDTH * SCREEN_HEIGHT; k++) {
                System.out.print(buffer[k]);
                if (k % SCREEN_WIDTH == SCREEN_WIDTH - 1)
                    System.out.println();
            }

            A += 0.05;
            B += 0.05;
            C += 0.01;
            // :TODO
            // try {
            // Thread.sleep(16);
            // } catch (Exception e) {
            // e.printStackTrace();
            // }
        }
    }
}
