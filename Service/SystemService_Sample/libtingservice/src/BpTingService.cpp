#include <stdint.h>

#include <utils/Log.h>
#include <binder/Parcel.h>

#include <BpTingService.h>


namespace android {

BpTingService::BpTingService(const sp<IBinder>& impl)
    : BpInterface<ITingService>(impl)
{ }

status_t BpTingService::test_function(const char *str)
{
    Parcel data, reply;

    data.writeInterfaceToken(ITingService::getInterfaceDescriptor());

    // Store string to be sent
    data.writeCString(str);

    status_t status = remote()->transact(TING_TEST_FUNC, data, &reply);

    if (status != NO_ERROR)
        ALOGE("%d: %s, TING Proxy Error!!!! ==> %s", __LINE__, __FUNCTION__, strerror(-status));
    else
        status = reply.readInt32();

    return status;
}

};   // namespace android
