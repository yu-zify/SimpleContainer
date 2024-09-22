echo path
WHOME=/data/user/0/com.simple.container/files

echo tmp
if  test ! -d $WHOME/tmp ; then
  mkdir $WHOME/tmp
fi

pam=$1

if [ "$pam" == "1" ]; then
	LD_PRELOAD=$WHOME/libbusybox.so.1.36.1 $WHOME/busybox tar -xvf $WHOME/core.tar -C $WHOME
	echo 1
elif [ "$pam" == "2" ]; then
	LD_PRELOAD=$WHOME/libbusybox.so.1.36.1 $WHOME/busybox tar -zxvf /sdcard/Download/debian_xfce.tar.gz -C $WHOME
	echo 2
fi
echo ok
