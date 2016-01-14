package hotLoadClass;

public class Test implements RefChange {
	private Say r;
	private Say refSayTow;
	public Test() {
		r = new RefSay();
		refSayTow = new RefSayTow();
		try {
			ClassWatcherService.GetInstance(this).StartServers();
			while (true) {
				r.say();
				refSayTow.say();
				Thread.sleep(3000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new Test();
	}

	@Override
	public void ReLoadClass(Object j) {
		if (j.getClass().getName().equals(r.getClass().getName())) {
			r = MyClassLoads.ReLoadClass(j);
		}
		if (j.getClass().getName().equals(refSayTow.getClass().getName())) {
			refSayTow = MyClassLoads.ReLoadClass(j);
		}
		System.err.println(j.getClass().getName() + "成功被热加载");
	}

}
