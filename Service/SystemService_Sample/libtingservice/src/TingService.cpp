#include <stdint.h>
#include <utils/Log.h>

#include <binder/IServiceManager.h>
#include <binder/IPCThreadState.h>
#include <binder/Parcel.h>

#include <BnTingService.h>
#include <TingService.h>

namespace android {

TingService::TingService()
{
    ALOGI("%d: %s Created!!!!", __LINE__, __FUNCTION__);
}

TingService::~TingService()
{
    ALOGI("%d: %s Destroyed!!!!", __LINE__, __FUNCTION__);
}

void TingService::instantiate()
{
    defaultServiceManager()->addService(String16("android.media.ITingService"), new TingService());
}

status_t TingService::test_function(const char *str)
{
    ALOGI("%d: %s ==> %s", __LINE__, __FUNCTION__, str);

    return NO_ERROR;
}

status_t TingService::onTransact( uint32_t code,
        const Parcel& data,
        Parcel* reply,
        uint32_t flags)
{
    return BnTingService::onTransact(code, data, reply, flags);
}

}; // namespace android
