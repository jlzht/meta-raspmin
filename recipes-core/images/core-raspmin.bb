SUMMARY = "core-raspmin-image"
DESCRIPTION = "Base raspmin image"
IMAGE_FEATURES += "ssh-server-dropbear debug-tweaks"

RASPMIN_CONNECTIVITY="dhcpcd wpa-supplicant linux-firmware-rpidistro-bcm43455"
CORE_IMAGE_EXTRA_INSTALL = "${RASPMIN_CONNECTIVITY}" 

IMAGE_INSTALL += " \
    openrc \
    packagegroup-core-boot \
    ${CORE_IMAGE_EXTRA_INSTALL} \
"

inherit core-image openrc-image

export IMAGE_BASENAME = "core-raspmin"

LICENSE = "MIT"

OPENRC_STACKED_RUNLEVELS += "logging:default"
OPENRC_SERVICES += " \
    sysinit:udev-trigger \
    default:udev-settle \
    default:dhcpcd \
    logging:busybox-klogd \
    logging:busybox-syslogd \
"

boot_to_logging() {
    local mgr=${@d.getVar('VIRTUAL-RUNTIME_init_manager')}

    if [ "${mgr}" = "sysvinit" ]; then
        sed -i '/^l[345]/s,default,logging,' ${IMAGE_ROOTFS}${sysconfdir}/inittab
    elif [ "${mgr}" = "busybox" ]; then
        sed -i 's/openrc default/openrc logging/' ${IMAGE_ROOTFS}${sysconfdir}/inittab
    elif [ "${mgr}" = "openrc-init" ]; then
        sed -i 's/^#\(rc_default_runlevel="\).*/\1logging"/' ${IMAGE_ROOTFS}${sysconfdir}/rc.conf
    fi
}

ROOTFS_POSTPROCESS_COMMAND += "${@bb.utils.contains('DISTRO_FEATURES', 'openrc', 'boot_to_logging; ', '', d)}"
