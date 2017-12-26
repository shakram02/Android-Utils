package shakram02.ahmed.androidutils;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * This is a modified version of the source.
 * source: https://stackoverflow.com/questions/4284224/android-hold-button-to-repeat-action
 * <p>
 * A class, that can be used as a TouchListener on any view (e.g. a Button).
 * <p>
 * <p>Interval is scheduled after the onClick completes, so it has to run fast.
 * If it runs slow, it does not generate skipped onClicks. Can be rewritten to
 * achieve this.
 */
public class RepeatListener implements OnTouchListener {

    private final View.OnClickListener clickListener;
    private Handler handler = new Handler();

    private int initialInterval;
    private final int normalInterval;

    private View downView;
    private Runnable viewDownHandler = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, normalInterval);
            clickListener.onClick(downView);
        }
    };

    private OnButtonReleaseListener viewUpHandler;

    /**
     * Interface for button release event
     */
    public interface OnButtonReleaseListener {
        void onRelease(View v);
    }

    /**
     * @param initialInterval The interval after first click event
     * @param normalInterval  The interval after second and subsequent click
     *                        events
     * @param clickListener   The OnClickListener, that will be called
     *                        periodically
     */
    public RepeatListener(int initialInterval, int normalInterval,
                          View.OnClickListener clickListener) {
        if (clickListener == null)
            throw new IllegalArgumentException("null runnable");
        if (initialInterval < 0 || normalInterval < 0)
            throw new IllegalArgumentException("negative interval");

        this.initialInterval = initialInterval;
        this.normalInterval = normalInterval;
        this.viewUpHandler = null;
        this.clickListener = clickListener;

    }

    public RepeatListener(int initialInterval, int normalInterval,
                          final View.OnClickListener onClickAction, final OnButtonReleaseListener onReleaseAction) {
        this(initialInterval, normalInterval, onClickAction);
        this.viewUpHandler = onReleaseAction;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        downView = view;
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacks(viewDownHandler);
                handler.postDelayed(viewDownHandler, initialInterval);

                downView.setPressed(true);
                clickListener.onClick(downView);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (viewUpHandler != null) {
                    viewUpHandler.onRelease(downView);
                }

                handler.removeCallbacks(viewDownHandler);
                downView.setPressed(false);
                downView = null;
                return true;
        }

        return false;
    }

}

