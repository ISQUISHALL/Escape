package com.dreamstone.core;

import com.dreamstone.world.Grid;

public class Dystopia implements Runnable {
	
	static Dystopia gameInstance;
	public Grid grid;
	private int ticks;
	private int frames;
	
	private int avgFPSCount;
	private int avgFPS;
	
	private Thread gameThread;
	private boolean running;
	
	protected Dystopia() {
		ticks = 0;
		frames = 0;
	}
	
	public synchronized void start() {
		if (running) return;
		running = true;
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	public synchronized void stop() {
		if (!running) return;
		running = false;
		try {
			gameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		
		double unproccessedSeconds = 0;
		long lastTime = System.nanoTime();
		double secondsPerTick = 1 / 60.0;
		int tickCount = 0;
		
		while (running) {
			long now = System.nanoTime();
			long passedTime = now - lastTime;
			lastTime = now;
			
			if (passedTime < 0) {
				passedTime = 0;
			}
			
			if (passedTime > 1_000_000_000) {
				passedTime = 1_000_000_000;
			}
			unproccessedSeconds += passedTime / 1_000_000_000.0;
			
			//boolean ticked = false;
			while (unproccessedSeconds > secondsPerTick) {
				tick();
				
				unproccessedSeconds -= secondsPerTick;
				//ticked = true;
				
				tickCount++;
				if (tickCount % 60 == 0) {
					avgFPSCount++;
					avgFPS += frames;
					
					System.out.println("Ticks: " + tickCount + ", Fps: " + frames);
					if (avgFPSCount == 5) {
						System.out.println("=========================\nAVERAGE FPS: " + avgFPS / avgFPSCount + "\n=========================");
						avgFPSCount = 0;
						avgFPS = 0;
					}
					lastTime += 1000;
					frames = 0;
					tickCount = 0;
				}
			}
			
			//Updates frames independently from the game logic (ticks).
			DisplayCarrier.getCanvas().render();
			frames++;
			
			//Controls the frames to update the same time as the game logic (ticks).
			/*if (ticked) {
				render();
				frames++;
			}*/
		}
	}
	
	public void tick() {
		ticks++;
//		System.out.println(Dystopia.getGame().getTickCount());
	}
	
	public int getTickCount() {
		return ticks;
	}
	
	public int getFrameRate() {
		return frames;
	}
	
	public static Dystopia getGame() {
		return gameInstance;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
}
