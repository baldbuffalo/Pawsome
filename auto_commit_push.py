import time
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
import subprocess

class ChangeHandler(FileSystemEventHandler):
    def on_modified(self, event):
        subprocess.run(["git", "add", "-A"])
        subprocess.run(["git", "commit", "-m", f"Auto commit: {time.strftime('%Y-%m-%d %H:%M:%S')}"])
        subprocess.run(["git", "push", "origin", "main"])

if __name__ == "__main__":
    path = "."  # Watch the current directory
    event_handler = ChangeHandler()
    observer = Observer()
    observer.schedule(event_handler, path, recursive=True)
    observer.start()
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        observer.stop()
    observer.join()
