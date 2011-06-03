package jade.android;

import jade.core.MicroRuntime;
import jade.util.Logger;
import jade.util.leap.Properties;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MicroRuntimeService extends Service {
	protected static final Logger logger = Logger
			.getMyLogger(RuntimeService.class.getName());

	private final IBinder binder = new MicroRuntimeServiceBinder(this);

	private String agentName;

	@Override
	public void onCreate() {
		logger.log(Logger.INFO, "JADE micro runtime service created");
	}

	@Override
	public void onDestroy() {
		logger.log(Logger.INFO, "JADE micro runtime service destroyed");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		logger.log(Logger.SEVERE,
				"JADE micro runtime service can only be used locally");

		throw new UnsupportedOperationException();
	}

	@Override
	public IBinder onBind(Intent intent) {
		logger.log(Logger.INFO, "JADE micro runtime service bound");

		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		logger.log(Logger.INFO, "JADE micro runtime service unbound");

		return false;
	}

	public void startAgentContainer(String host, int port,
			RuntimeCallback<Void> callback) {
		Properties properties = RuntimeHelper.createProfileProperties(host,
				port);

		startAgentContainer(properties, callback);
	}

	public void startAgentContainer(Properties properties,
			RuntimeCallback<Void> callback) {
		final Properties finalProperties = properties;

		final RuntimeCallback<Void> finalCallback = callback;

		RuntimeHelper.completeProfileProperties(properties);

		new Thread() {
			@Override
			public void run() {
				try {
					logger.log(Logger.INFO, "Creating micro agent container");

					MicroRuntime.startJADE(finalProperties, null);

					finalCallback.notifySuccess(logger, null);

					logger.log(Logger.INFO, "Agent container created");
				} catch (Throwable t) {
					logger.log(Logger.INFO,
							"Cannot create micro agent container with message: "
									+ t.getMessage());

					finalCallback.notifyFailure(logger, t);
				}
			}
		}.start();
	}

	public void stopAgentContainer(RuntimeCallback<Void> callback) {
		final RuntimeCallback<Void> finalCallback = callback;

		new Thread() {
			@Override
			public void run() {
				try {
					logger.log(Logger.INFO, "Stopping micro agent container");

					MicroRuntime.stopJADE();

					finalCallback.notifySuccess(logger, null);

					logger.log(Logger.INFO, "Agent container stopped");
				} catch (Throwable t) {
					logger.log(Logger.INFO,
							"Cannot stop micro agent container with message: "
									+ t.getMessage());

					finalCallback.notifyFailure(logger, t);
				}
			}
		}.start();
	}

	public void startAgent(String nickname, String className, Object[] args,
			RuntimeCallback<Void> callback) {
		final RuntimeCallback<Void> finalCallback = callback;

		final String finalNickname = nickname;

		final String finalClassName = className;

		final Object[] finalArgs = args;

		new Thread() {
			@Override
			public void run() {
				try {
					logger.log(Logger.INFO, "Starting agent");

					agentName = finalNickname;

					MicroRuntime.startAgent(finalNickname, finalClassName,
							finalArgs);

					finalCallback.notifySuccess(logger, null);

					logger.log(Logger.INFO, "Agent started");
				} catch (Throwable t) {
					logger.log(Logger.INFO, "Cannot start agent with message: "
							+ t.getMessage());

					finalCallback.notifyFailure(logger, t);
				}
			}
		}.start();
	}

	public void killAgent(RuntimeCallback<Void> callback) {
		final RuntimeCallback<Void> finalCallback = callback;

		new Thread() {
			@Override
			public void run() {
				try {
					logger.log(Logger.INFO, "Killing agent");

					MicroRuntime.killAgent(agentName);

					agentName = null;

					finalCallback.notifySuccess(logger, null);

					logger.log(Logger.INFO, "Agent killed");
				} catch (Throwable t) {
					logger.log(Logger.INFO, "Cannot kill agent with message: "
							+ t.getMessage());

					finalCallback.notifyFailure(logger, t);
				}
			}
		}.start();
	}
}
