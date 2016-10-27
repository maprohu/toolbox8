package org.bluez;
import org.freedesktop.DBus.Deprecated;
import org.freedesktop.dbus.DBusInterface;
public interface MediaControl1 extends DBusInterface
{

  @Deprecated("true")
  public void Play();
  @Deprecated("true")
  public void Pause();
  @Deprecated("true")
  public void Stop();
  @Deprecated("true")
  public void Next();
  @Deprecated("true")
  public void Previous();
  @Deprecated("true")
  public void VolumeUp();
  @Deprecated("true")
  public void VolumeDown();
  @Deprecated("true")
  public void FastForward();
  @Deprecated("true")
  public void Rewind();

}
