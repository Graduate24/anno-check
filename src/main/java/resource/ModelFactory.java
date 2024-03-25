package resource;

import spoon.Launcher;
import spoon.reflect.CtModel;

public class ModelFactory {
    private static CtModel model;

    public static CtModel init(String path) {
        if (model == null) {
            Launcher launcher = new Launcher();
            launcher.addInputResource(path);
            model = launcher.buildModel();
        }
        return model;
    }

    public static CtModel getModel() {
        return model;
    }
}
