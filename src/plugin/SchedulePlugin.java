package plugin;

import static com.jfinal.aop.Enhancer.enhance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import com.jfinal.core.JFinal;
import com.jfinal.plugin.IPlugin;

import engine.SnakerEngine;
import engine.Workflow;

/*
 * 定时执行类
 */
public class SchedulePlugin implements IPlugin {

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public boolean start() {
		removeWorkflowInstance();
		return true;
	}

	public boolean stop() {
		return true;
	}

	/**
	 * 清理流程实例
	 * @return
	 */
	public boolean removeWorkflowInstance() {
		final SnakerEngine engine = enhance(SnakerEngine.class);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 3);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				long curr = System.currentTimeMillis();
				StringBuilder sb = new StringBuilder("\nRemove workflow instance ---- ").append(dateFormat.format(curr)).append(" ------------------------------\n");
				Iterator<Workflow> iterator = engine.getInstances().iterator();
				while (iterator.hasNext()) {
					Workflow instance = iterator.next();
					if (curr - instance.getInstanceCreateDate().getTime() >= 1000 * 60 * 60 * 24 * 7) {
						sb.append("Remove      : ").append(instance.getProcessName()).append("(").append(dateFormat.format(instance.getInstanceCreateDate())).append(")\n");
						iterator.remove();
					}
				}
				sb.append("--------------------------------------------------------------------------------\n");
				if (JFinal.me().getConstants().getDevMode())
					System.out.print(sb.toString());
			}
		}, calendar.getTime(), 1000 * 60 * 60 * 24);
		return true;
	}

}
