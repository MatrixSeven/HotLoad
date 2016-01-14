package hotLoadClass;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;

/**
 * @author seven class文件监控器
 */
public class ClassWatcherService implements Runnable {

	private WatchService watcher;
	// 监控目录
	public static String path = "C:\\Users\\seven\\Desktop\\java\\UpdateLib";
	private HashSet<RefChange> RefChangeSet = null;
	private static ClassWatcherService classWatcherService = null;
	private Object ClassTemp;
	private boolean IsRuner = false;

	private ClassWatcherService() {
		try {
			watcher = FileSystems.getDefault().newWatchService();
			Paths.get(path).register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
			RefChangeSet = new HashSet<RefChange>(20);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ClassWatcherService GetInstance(RefChange r) {
		if (classWatcherService == null)
			classWatcherService = new ClassWatcherService();
		classWatcherService.RefChangeSet.add(r);
		return classWatcherService;
	}

	public boolean IsRun() {
		return IsRuner;
	}

	public ClassWatcherService StartServers() {
		if (!IsRuner)
			new Thread(this).start();
		return this;
	}

	/**
	 * 监控文件
	 */
	private void handleEvents() {
		while (true) {
			try {
				WatchKey key = watcher.take();
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					if (kind == OVERFLOW) {
						continue;
					}
					@SuppressWarnings("unchecked")
					WatchEvent<Path> e = (WatchEvent<Path>) event;
					Path fileName = e.context();
					System.err.println("发现目录下有Class发生变化.进行热加载" + path + "\\" + fileName);
					ClassTemp = MyClassLoads.GetInstance().FindNewClass(path + "\\" + fileName);
					RefChangeSet.stream().forEach(a -> a.ReLoadClass(ClassTemp));
				}
				if (!key.reset()) {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 * @see 禁止外部方法直接调用该方法,只能通过 StartServers启动
	 ***/
	@Deprecated
	@Override
	public void run() {
		if (!IsRuner) {
			IsRuner = !IsRuner;
			handleEvents();
		}
		System.err.println("文件监听已运行");
	}
}