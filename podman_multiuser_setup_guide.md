# Podman 多用戶環境配置指南

## I. 基礎設置

### 1. 創建共享群組

```bash
sudo groupadd container-users
```

### 2. 創建共享存儲

```bash
sudo mkdir /var/lib/shared-containers
sudo chown :container-users /var/lib/shared-containers
sudo chmod 775 /var/lib/shared-containers
```

### 3. 設置 ACL

```bash
sudo setfacl -R -m g:container-users:rwx /var/lib/shared-containers
sudo setfacl -R -d -m g:container-users:rwx /var/lib/shared-containers
```

## II. 用戶配置

### 1. 創建 sysmon 用戶

```bash
sudo useradd -m -s /bin/bash sysmon
sudo passwd sysmon
sudo usermod -aG container-users sysmon
```

### 2. 配置 Podman 存儲

```bash
sudo -u sysmon mkdir -p /home/sysmon/.config/containers
cat << EOF | sudo tee /home/sysmon/.config/containers/storage.conf
[storage]
driver = "overlay"
graphroot = "/var/lib/shared-containers"
EOF
sudo chown sysmon:sysmon /home/sysmon/.config/containers/storage.conf
```

### 3. 設置環境變量

```bash
sudo -u sysmon tee -a /home/sysmon/.bashrc << EOF

# Podman configuration
export XDG_RUNTIME_DIR=/run/user/$(id -u)
export DOCKER_HOST="unix://$XDG_RUNTIME_DIR/podman/podman.sock"
EOF
```

## III. 資源管理

### 1. 設置 cgroups

```bash
sudo cgcreate -g cpu,memory:sysmon
sudo cgset -r cpu.shares=512 sysmon
sudo cgset -r memory.limit_in_bytes=4G sysmon
sudo cgclassify -g cpu,memory:sysmon $(pgrep -u sysmon)
```

## IV. 權限控制

### 1. 創建管理腳本

```bash
sudo tee /usr/local/bin/manage-container << 'EOF'
#!/bin/bash
action=$1
container=$2
sysmon_user=$3

log_file="/var/log/container-management.log"

log_action() {
    echo "$(date): User $USER performed $action on container $container for $sysmon_user" >> $log_file
}

check_permission() {
    # 實現權限檢查邏輯
    return 0
}

if ! check_permission; then
    echo "Permission denied"
    log_action "DENIED"
    exit 1
fi

case $action in
  start|stop|restart)
    sudo -u container-manager sudo -u $sysmon_user podman $action $container
    ;;
  list)
    sudo -u container-manager sudo -u $sysmon_user podman ps -a
    ;;
  inspect)
    sudo -u container-manager sudo -u $sysmon_user podman inspect $container
    ;;
  *)
    echo "Usage: $0 {start|stop|restart|list|inspect} [container_name] [sysmon_user]"
    exit 1
    ;;
esac

log_action
EOF

sudo chmod 755 /usr/local/bin/manage-container
```

### 2. 設置 sudo 規則

```bash
sudo visudo -f /etc/sudoers.d/container-manager
```

添加：

```
container-manager ALL=(sysmon) NOPASSWD: /usr/bin/podman start *, /usr/bin/podman stop *, /usr/bin/podman restart *, /usr/bin/podman ps, /usr/bin/podman inspect *
```

### 3. 配置 root 用戶權限

```bash
sudo visudo -f /etc/sudoers.d/root-container-management
```

添加：

```
root ALL=(ALL) NOPASSWD: /usr/local/bin/manage-container
```

## V. 安全增強

### 1. SELinux 配置（如適用）

```bash
cat << EOF > container_manager.te
module container_manager 1.0;

require {
    type container_manager_t;
    type podman_t;
    class process transition;
}

allow container_manager_t podman_t:process transition;
EOF

make -f /usr/share/selinux/devel/Makefile container_manager.pp
semodule -i container_manager.pp

sudo semanage login -a -s user_u sysmon
```

### 2. AppArmor 配置（如適用）

```bash
sudo tee /etc/apparmor.d/usr.local.bin.manage-container << 'EOF'
#include <tunables/global>

/usr/local/bin/manage-container {
  #include <abstractions/base>

  /usr/bin/sudo ix,
  /usr/bin/podman ix,
  /var/log/container-management.log w,
}
EOF

sudo apparmor_parser -r /etc/apparmor.d/usr.local.bin.manage-container

sudo tee /etc/apparmor.d/user.sysmon << EOF
#include <tunables/global>

profile user.sysmon {
  #include <abstractions/base>
  #include <abstractions/user-tmp>

  /home/sysmon/** rwl,
  /var/lib/shared-containers/** rw,
  /usr/bin/podman rix,
}
EOF

sudo apparmor_parser -r /etc/apparmor.d/user.sysmon
```

## VI. 日誌和審計

### 1. 配置系統日誌

```bash
sudo tee -a /etc/rsyslog.d/sysmon.conf << EOF
:programname, isequal, "sysmon" /var/log/sysmon.log
& stop
EOF

sudo systemctl restart rsyslog
```

### 2. 設置日誌輪轉

```bash
sudo tee /etc/logrotate.d/container-management << EOF
/var/log/container-management.log {
    rotate 7
    daily
    compress
    missingok
    notifempty
}
EOF
```

### 3. 審計腳本

```bash
sudo tee /usr/local/bin/container-audit << 'EOF'
#!/bin/bash

echo "Container Management Audit Report"
echo "================================"

echo "Sudo operations summary:"
grep "podman" /var/log/auth.log | awk '{print $1, $2, $3, $6, $11, $12, $13}' | sort | uniq -c

echo -e "\nPodman operations summary:"
grep "podman" /var/log/container-management.log | awk '{print $1, $2, $3, $6, $8, $10}' | sort | uniq -c

# 可以添加更多審計邏輯

# 郵件發送（如需要）
# mail -s "Container Audit Report" admin@example.com < /tmp/audit_report.txt
EOF

sudo chmod +x /usr/local/bin/container-audit
```

## VII. 使用說明

1. root 用戶管理容器：
   ```
   sudo manage-container start my_container sysmon
   sudo manage-container stop my_container sysmon
   sudo manage-container list sysmon
   ```

2. 審計容器操作：
   ```
   sudo /usr/local/bin/container-audit
   ```

3. 檢查日誌：
   ```
   sudo tail -f /var/log/container-management.log
   sudo tail -f /var/log/sysmon.log
   ```

## VIII. 安全注意事項

1. 定期更新系統和 Podman。
2. 定期審核用戶權限和訪問日誌。
3. 根據需求調整 cgroups 限制。
4. 定期檢查和更新 SELinux/AppArmor 策略。
5. 考慮實施網絡隔離策略。
6. 定期備份重要數據和配置。​​​​​​​​​​​​​​​​