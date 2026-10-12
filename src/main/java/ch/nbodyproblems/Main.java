package ch.nbodyproblems;

import ch.nbodyproblems.rendering.Renderer;

public class Main {
    static void main(String[] args) {
        Renderer renderer = new Renderer(1280, 720);
        renderer.init();
        renderer.loop();
        renderer.cleanup();
    }
}
