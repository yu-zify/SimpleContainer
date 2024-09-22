WHOME=/data/user/0/com.simple.container/files
echo 888 > $WHOME/out
unset LD_PRELOAD
echo startproot

PROOT_TMP_DIR=$WHOME/tmp $WHOME/proot -0 -l --kill-on-exit -r $WHOME/test -b /dev -b /proc -b /sys -b $WHOME/tmp:/dev/shm -b $WHOME/oproc/stat:/proc/stat -w /root /usr/bin/su - root 
#-c " service dbus start ; Xvnc :0 -SecurityTypes=none & PULSE_SERVER=127.0.0.1 DISPLAY=:0 xfce4-session & while true ; do echo 1; sleep 1 ; done"