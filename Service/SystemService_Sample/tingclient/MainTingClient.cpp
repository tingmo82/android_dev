#include <stdint.h>
#include <utils/Log.h>

#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>

#include <ITingService.h>

using namespace android;

int main(int argc __unused, char **argv __unused)
{
    ALOGI("%s ==> Ting Client is startring now", __FUNCTION__);

    sp<IServiceManager> sm = defaultServiceManager();
    sp<IBinder> binder;
    sp<ITingService> service;

    do
    {
        binder = sm->getService(String16("android.media.ITingService"));

        if (binder != 0)
            break;

        ALOGI("%s ==> Ting Service is not working, waiting ...", __FUNCTION__);
        usleep(500000);

    } while (true);

    service = interface_cast<ITingService>(binder);

    service->test_function("Test Function was invoked by Client App");
}
