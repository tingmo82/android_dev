#ifndef ANDROID_ITINGSERVICE_H
#define ANDROID_ITINGSERVICE_H

#include <stdint.h>

#include <utils/RefBase.h>
#include <binder/IInterface.h>

namespace android {

// RPC Code
// TODO : Add some RPC codes
enum
{
    TING_TEST_FUNC = IBinder::FIRST_CALL_TRANSACTION,
    TING_INIT
};

class ITingService : public IInterface
{
public: 
	DECLARE_META_INTERFACE(TingService);

    // TODO : Add some RPC Service functions
    virtual status_t test_function(const char *str) = 0;
};

}; // namespace android

#endif //ANDROID_ITINGSERVICE_H
