#include <stdint.h>
#include <utils/Log.h>

#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>

#include <TingService.h>

using namespace android;

int main(int argc __unused, char **argv __unused)
{
    TingService::instantiate();
    ProcessState::self()->startThreadPool();
    ALOGI("%s ==> Ting server is startring now", __FUNCTION__);
    IPCThreadState::self()->joinThreadPool();
}
