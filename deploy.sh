#!/usr/bin/env bash
# Deploy TeamCode to the Control Hub over Wi-Fi adb.
#
# Handles the two things that keep breaking:
#   1. a stale/offline transport (adb connect alone will NOT fix it)
#   2. the emulator on the shared adb server making the target ambiguous
#
# Usage:  ./deploy.sh

HUB="${HUB:-192.168.43.1:5555}"
ATTEMPTS="${ATTEMPTS:-3}"

export ANDROID_SERIAL="$HUB"

reconnect() {
  adb disconnect "$HUB" >/dev/null 2>&1
  adb connect "$HUB"    >/dev/null 2>&1
  for _ in $(seq 1 15); do
    if [ "$(adb -s "$HUB" get-state 2>/dev/null)" = "device" ]; then
      return 0
    fi
    sleep 1
  done
  return 1
}

for attempt in $(seq 1 "$ATTEMPTS"); do
  echo "=== attempt $attempt/$ATTEMPTS ==="

  if ! reconnect; then
    echo "hub did not come online. Check:"
    echo "  - Mac is on the hub's Wi-Fi   : networksetup -getairportnetwork en0"
    echo "  - hub is reachable            : ping -c 3 192.168.43.1"
    echo "  - Robot Controller app awake on the hub"
    exit 1
  fi

  echo "hub online, installing..."
  if ./gradlew installDebug; then
    echo
    echo "=== deployed ==="
    exit 0
  fi

  echo "attempt $attempt failed (link probably dropped mid-push); retrying..."
  sleep 2
done

echo
echo "Failed after $ATTEMPTS attempts. The Wi-Fi link is dropping during transfer."
echo "Move the laptop next to the hub, or fix the USB cable and deploy wired."
exit 1
