echo path
WHOME=/data/user/0/com.simple.container/files

echo tmp
if  test ! -d $WHOME/tmp ; then
  mkdir $WHOME/tmp
fi

if  test ! -d $WHOME/extra ; then
  mkdir $WHOME/extra
fi

pam=$1

if [ "$pam" == "1" ]; then
	LD_PRELOAD=$WHOME/libbusybox.so.1.36.1 $WHOME/busybox tar -xvf $WHOME/core -C $WHOME
	echo 1
elif [ "$pam" == "2" ]; then
  if test -f /sdcard/Download/debian_xfce.tar.gz ; then
    mv /sdcard/Download/debian_xfce.tar.gz $WHOME
  fi
	LD_PRELOAD=$WHOME/libbusybox.so.1.36.1 $WHOME/busybox tar -zxvf $WHOME/debian_xfce.tar.gz -C $WHOME
	echo 2
elif [ "$pam" == "3" ]; then
  LD_PRELOAD=$WHOME/libbusybox.so.1.36.1 $WHOME/busybox tar -zxvf $WHOME/extra/extra.tar.gz -C $WHOME/test
fi
echo ok
