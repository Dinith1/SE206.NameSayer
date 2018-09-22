package namesayer;

import java.lang.reflect.Field;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

public class TooltipHandler {
	
	// Taken from https://stackoverflow.com/questions/26854301/how-to-control-the-javafx-tooltips-delay
	// Makes tooltips appear/disappear faster
	public void hackTooltipStartTiming(Tooltip tooltip) {
		try {
			Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
			fieldBehavior.setAccessible(true);
			Object objBehavior = fieldBehavior.get(tooltip);

			Field actTimer = objBehavior.getClass().getDeclaredField("activationTimer");
			actTimer.setAccessible(true);
			Timeline objActTimer = (Timeline) actTimer.get(objBehavior);

			objActTimer.getKeyFrames().clear();
			objActTimer.getKeyFrames().add(new KeyFrame(new Duration(200)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
