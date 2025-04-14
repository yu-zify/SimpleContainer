WHOME=/data/user/0/com.simple.container/files
echo 888 > $WHOME/out
unset LD_PRELOAD
echo startproot

pam=$1
args="service dbus start ; /opt/dri3vnc/bin/Xvnc :0 -SecurityTypes=none & PULSE_SERVER=127.0.0.1 DISPLAY=:0 xfce4-session 2>/dev/null & /root/noVNC/utils/novnc_proxy --vnc localhost:5900 "

args2="/sbin/sshd & ping 127.0.0.1"

args3="service dbus start ; /opt/dri3vnc/bin/Xvnc :0 -SecurityTypes=none & PULSE_SERVER=127.0.0.1 DISPLAY=:0 XDG_RUNTIME_DIR=/tmp startplasma-x11 2>/dev/null & /root/noVNC/utils/novnc_proxy --vnc localhost:5900 "

PROOT_TMP_DIR=$WHOME/tmp $WHOME/proot -0 -l --kill-on-exit -r $WHOME/test -b /dev -b /proc -b /sys -b $WHOME/tmp:/dev/shm -b $WHOME/oproc/stat:/proc/stat -b $WHOME/oproc/dev/dri:/dev/dri -b $WHOME/oproc/pci:/proc/bus/pci -w /root /bin/su - root -c "$args3"