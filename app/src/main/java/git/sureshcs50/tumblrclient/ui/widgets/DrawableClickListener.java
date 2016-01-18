package git.sureshcs50.tumblrclient.ui.widgets;

public interface DrawableClickListener {
    public void onClick(DrawablePosition target);

    public static enum DrawablePosition {TOP, BOTTOM, LEFT, RIGHT}
}
