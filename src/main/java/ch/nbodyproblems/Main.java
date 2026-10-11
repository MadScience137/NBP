package ch.nbodyproblems;

public class Main {
    static void main(String[] args) {
        Renderer renderer = new Renderer(800, 600);
        renderer.init();
        renderer.loop();
        renderer.cleanup();
    }
}
