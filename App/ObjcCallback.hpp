#pragma once
#ifndef _OBJC_CALLBACK_H_
#define _OBJC_CALLBACK_H_

#include <objc/objc.h>

template<typename Signature> class objc_callback;

template<typename R, typename... Ts>
class objc_callback<R(Ts...)>
{
public:
  typedef R (*func)(id, SEL, Ts...);
  
  objc_callback() {}
  
  objc_callback(SEL sel, id obj)
  : sel_(sel)
  , obj_(obj)
  , fun_((func)[obj methodForSelector:sel])
  {
  }
  
  bool is_set() const {
    return fun_ != nullptr && sel_ != nullptr && obj_ != nullptr;
  }
  
  inline R operator ()(Ts... vs)
  {
    return fun_(obj_, sel_, vs...);
  }
private:
  SEL sel_;
  id obj_;
  func fun_;
};

#endif // _OBJC_CALLBACK_H_
