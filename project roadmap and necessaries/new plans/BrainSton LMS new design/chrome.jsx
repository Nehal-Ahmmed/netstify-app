// chrome.jsx — app bar, bottom nav, drawer, gates, common scaffolds

// ──────────────────────────────────────────────────────────────
// Phone shell — wraps an iOS device & content, no scrolling outside
// ──────────────────────────────────────────────────────────────
function Phone({ children, dark=false, statusDark, time='9:41' }) {
  const sd = statusDark !== undefined ? statusDark : dark;
  return (
    <IOSDevice dark={dark}>
      <div style={{ height: '100%', display: 'flex', flexDirection: 'column', position: 'relative', overflow:'hidden' }}>
        <div style={{ position: 'absolute', top: 0, left: 0, right: 0, zIndex: 30 }}>
          <PhoneStatus dark={sd} time={time}/>
        </div>
        {children}
      </div>
    </IOSDevice>
  );
}

// App bar — MediShark style
function AppBar({
  variant='default',   // default | title | logo | transparent
  title,
  onMenu, onBack, onSearch, onCart, onAvatar,
  authed=true, cartCount=0, dark=false,
  trailing,
  bg=T.surface, ink=T.ink, divider=true,
}) {
  const isDark = bg === T.surfaceDk || dark;
  const fg = isDark ? '#fff' : ink;
  const sub = isDark ? 'rgba(255,255,255,0.6)' : T.ink50;

  return (
    <div style={{
      paddingTop: 54, // under status bar
      paddingBottom: 10,
      paddingLeft: 16, paddingRight: 16,
      background: bg,
      borderBottom: divider ? `1px solid ${isDark ? 'rgba(255,255,255,0.08)' : T.hair2}` : 'none',
      display: 'flex', alignItems: 'center', gap: 12,
      position: 'relative', zIndex: 20,
    }}>
      {/* Leading */}
      <button onClick={onBack || onMenu} style={{
        width: 38, height: 38, borderRadius: 10, border: 0, padding: 0,
        background: 'transparent', color: fg, cursor: 'pointer',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
      }}>
        {onBack ? <I.back/> : <I.menu/>}
      </button>

      {/* Center / title */}
      {variant === 'logo' ? (
        <div style={{ flex: 1, display:'flex', alignItems:'center', gap:8 }}>
          <BrandMark size={26} label/>
        </div>
      ) : variant === 'title' ? (
        <div style={{ flex: 1, fontFamily: T.sans, fontSize: 17, fontWeight: 600, color: fg, letterSpacing: -0.2 }}>{title}</div>
      ) : (
        <div style={{ flex: 1, display:'flex', alignItems:'center', gap:8 }}>
          <BrandMark size={26}/>
          <div>
            <div style={{ fontFamily: T.sans, fontSize: 14, fontWeight: 700, color: fg, letterSpacing: -0.2, lineHeight: 1 }}>MediShark</div>
            <div style={{ fontFamily: T.sans, fontSize: 10, color: sub, marginTop: 2 }}>Learn medicine, smarter</div>
          </div>
        </div>
      )}

      {/* Trailing */}
      {trailing !== undefined ? trailing : (
        <div style={{ display:'flex', alignItems:'center', gap: 2 }}>
          {!authed && (
            <Btn size="sm" variant="primary" onClick={onAvatar}>Sign in</Btn>
          )}
          {authed && (
            <>
              {onSearch && (
                <button onClick={onSearch} style={iconBtn(fg)}><I.search/></button>
              )}
              {onCart && (
                <button onClick={onCart} style={{ ...iconBtn(fg), position:'relative' }}>
                  <I.cart/>
                  {cartCount > 0 && (
                    <span style={{
                      position: 'absolute', top: 4, right: 4, minWidth: 16, height: 16,
                      padding: '0 4px', borderRadius: 8, background: T.coral, color:'#fff',
                      fontFamily: T.sans, fontSize: 10, fontWeight: 700,
                      display:'flex', alignItems:'center', justifyContent:'center',
                    }}>{cartCount}</span>
                  )}
                </button>
              )}
              {onAvatar && (
                <button onClick={onAvatar} style={{ ...iconBtn(fg), padding: 0 }}>
                  <Avatar name="Ayesha Rahman" size={30}/>
                </button>
              )}
            </>
          )}
        </div>
      )}
    </div>
  );
}

const iconBtn = (color) => ({
  width: 38, height: 38, borderRadius: 10, border: 0, padding: 0,
  background: 'transparent', color, cursor: 'pointer',
  display: 'flex', alignItems: 'center', justifyContent: 'center',
});

// ──────────────────────────────────────────────────────────────
// Bottom navigation (floating)
// ──────────────────────────────────────────────────────────────
function BottomNav({ active='home', onChange }) {
  const items = [
    { id: 'home', label: 'Home', icon: I.home, iconF: I.homeF },
    { id: 'courses', label: 'Courses', icon: I.grad, iconF: I.gradF },
    { id: 'articles', label: 'Articles', icon: I.news, iconF: I.newsF },
    { id: 'study', label: 'Study', icon: I.study, iconF: I.studyF },
  ];
  return (
    <div style={{
      position: 'absolute', left: 16, right: 16, bottom: 16, zIndex: 40,
      background: T.surface,
      borderRadius: 26,
      padding: 8,
      boxShadow: '0 1px 0 rgba(15,26,24,0.04), 0 16px 40px -12px rgba(15,26,24,0.25)',
      border: `1px solid ${T.hair2}`,
      display: 'flex', justifyContent: 'space-between',
    }}>
      {items.map(it => {
        const on = it.id === active;
        const Ic = on ? it.iconF : it.icon;
        return (
          <button key={it.id} onClick={() => onChange && onChange(it.id)} style={{
            flex: 1, height: 48, border: 0, padding: 0, borderRadius: 18,
            background: on ? T.brandSoft : 'transparent',
            display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6,
            cursor: 'pointer', transition: 'all .2s', WebkitTapHighlightColor:'transparent',
            color: on ? T.brandDeep : T.ink50,
          }}>
            <Ic width={20} height={20}/>
            {on && <span style={{ fontFamily: T.sans, fontSize: 13, fontWeight: 600 }}>{it.label}</span>}
          </button>
        );
      })}
    </div>
  );
}

// Bottom safe area spacer (pushes content above floating nav + home indicator)
function NavSpacer({ h=104 }) {
  return <div style={{ height: h, flexShrink: 0 }}/>;
}

// ──────────────────────────────────────────────────────────────
// Side drawer (overlay)
// ──────────────────────────────────────────────────────────────
function Drawer({ open, onClose, authed=true }) {
  return (
    <>
      {/* Backdrop */}
      <div onClick={onClose} style={{
        position: 'absolute', inset: 0, zIndex: 70,
        background: open ? 'rgba(15,26,24,0.45)' : 'transparent',
        pointerEvents: open ? 'auto' : 'none',
        transition: 'background .2s',
        backdropFilter: open ? 'blur(2px)' : 'none',
      }}/>
      {/* Panel */}
      <div style={{
        position: 'absolute', left: 0, top: 0, bottom: 0, width: '78%', zIndex: 80,
        background: T.surface, transform: open ? 'translateX(0)' : 'translateX(-100%)',
        transition: 'transform .25s cubic-bezier(.2,.7,.3,1)',
        borderRadius: '0 24px 24px 0', overflow: 'hidden',
        display: 'flex', flexDirection: 'column',
      }}>
        {/* Header */}
        <div style={{ padding: '60px 20px 18px', background: T.brand, color: '#fff' }}>
          <div style={{ display:'flex', alignItems:'center', gap: 12 }}>
            {authed ? (
              <>
                <Avatar name="Ayesha Rahman" size={48} ring/>
                <div>
                  <div style={{ fontFamily: T.sans, fontSize: 16, fontWeight: 600 }}>Ayesha Rahman</div>
                  <div style={{ fontFamily: T.sans, fontSize: 12, opacity: 0.85, marginTop: 2 }}>Dhaka Medical College</div>
                </div>
              </>
            ) : (
              <>
                <div style={{ width:48, height:48, borderRadius:'50%', background:'rgba(255,255,255,0.15)', display:'flex', alignItems:'center', justifyContent:'center', color:'#fff' }}><I.user/></div>
                <div>
                  <div style={{ fontFamily: T.sans, fontSize: 15, fontWeight: 600 }}>Guest</div>
                  <Btn size="sm" variant="secondary" style={{ marginTop:8, background:'#fff', color:T.brand }}>Sign in</Btn>
                </div>
              </>
            )}
          </div>
        </div>

        {/* Nav */}
        <div style={{ flex: 1, overflow: 'auto', padding: '12px 8px' }}>
          {[
            { icon: I.grad, label: 'Courses' },
            { icon: I.study, label: 'Study' },
            { icon: I.heart, label: 'Wishlist' },
            { icon: I.news, label: 'Articles' },
          ].map((it, i) => (
            <DrawerItem key={i} icon={it.icon} label={it.label}/>
          ))}

          <DrawerSep label="Settings"/>
          <DrawerItem icon={I.gear} label="Settings"/>
          <DrawerItem icon={I.globe} label="Language" trailing={<Chip tone="soft" style={{padding:'4px 10px', fontSize:11}}>EN</Chip>}/>

          <DrawerSep label="Information"/>
          <DrawerItem icon={I.star} label="Testimonials"/>
          <DrawerItem icon={I.lock} label="Privacy Policy"/>
          <DrawerItem icon={I.doc} label="Terms of Service"/>
        </div>

        {/* Footer */}
        <div style={{ borderTop: `1px solid ${T.hair2}`, padding: 12, display:'flex', gap: 8 }}>
          <Btn size="sm" variant="secondary" full>About Us</Btn>
          <Btn size="sm" variant="secondary" full>Contact Us</Btn>
        </div>
      </div>
    </>
  );
}

function DrawerItem({ icon: Ic, label, trailing }) {
  return (
    <div style={{
      display:'flex', alignItems:'center', gap: 14,
      padding: '12px 14px', borderRadius: 12, cursor:'pointer',
      color: T.ink70,
    }}
      onMouseEnter={e => e.currentTarget.style.background = T.surface2}
      onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
    >
      <div style={{ color: T.brand, display:'flex' }}><Ic/></div>
      <div style={{ flex:1, fontFamily: T.sans, fontSize: 15, fontWeight: 500, color: T.ink }}>{label}</div>
      {trailing}
    </div>
  );
}

function DrawerSep({ label }) {
  return (
    <div style={{
      fontFamily: T.mono, fontSize: 10, letterSpacing: 1, color: T.ink50,
      textTransform: 'uppercase', padding: '18px 14px 6px',
    }}>{label}</div>
  );
}

// ──────────────────────────────────────────────────────────────
// AuthBlockView — replaces gated screen bodies
// ──────────────────────────────────────────────────────────────
function AuthBlock({ variant='general' }) {
  const v = {
    study:    { icon: I.book, title: 'Sign in to study', desc: 'Continue your enrolled courses, track progress, and download lessons for offline.', accent: T.brand },
    profile:  { icon: I.user, title: 'Your profile awaits', desc: 'Sign in to manage your account, orders, and devices.', accent: T.brand },
    cart:     { icon: I.cart, title: 'Your cart is private', desc: 'Sign in to save items, check out, and access your purchases.', accent: T.coral },
    wishlist: { icon: I.heart,title: 'Save what inspires you', desc: 'Sign in to wishlist courses and revisit them anytime.', accent: T.coral },
    general:  { icon: I.lock, title: 'Members only', desc: 'Sign in or create an account to continue.', accent: T.brand },
  }[variant];
  const Ic = v.icon;
  return (
    <div style={{ flex:1, padding: '40px 24px', display:'flex', flexDirection:'column', alignItems:'center', justifyContent:'center', textAlign:'center' }}>
      <div style={{
        width: 88, height: 88, borderRadius: 26, background: T.brandSoft, color: v.accent,
        display:'flex', alignItems:'center', justifyContent:'center', marginBottom: 24,
        position:'relative',
      }}>
        <div style={{ position:'absolute', inset:-8, borderRadius: 32, border: `1.5px dashed ${T.brandSoft}` }}/>
        <Ic width={36} height={36}/>
      </div>
      <div style={{ fontFamily: T.serif, fontSize: 28, color: T.ink, letterSpacing: -0.3, marginBottom: 8 }}>{v.title}</div>
      <div style={{ fontFamily: T.sans, fontSize: 14, color: T.ink50, lineHeight: 1.5, maxWidth: 280, marginBottom: 28 }}>{v.desc}</div>
      <div style={{ display:'flex', flexDirection:'column', gap: 10, width: '100%', maxWidth: 280 }}>
        <Btn variant="primary" size="lg" full>Sign In</Btn>
        <Btn variant="ghost" size="lg" full>Create account</Btn>
      </div>
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
// Search bar (pill)
// ──────────────────────────────────────────────────────────────
function SearchBar({ placeholder='Search…', value='', trailing, dark=false }) {
  return (
    <div style={{
      display:'flex', alignItems:'center', gap: 10,
      height: 46, padding: '0 16px',
      background: dark ? 'rgba(255,255,255,0.06)' : T.surface,
      borderRadius: T.r.pill, border: `1px solid ${dark ? 'rgba(255,255,255,0.08)' : T.hair}`,
    }}>
      <I.search style={{ color: dark ? 'rgba(255,255,255,0.6)' : T.ink50 }}/>
      <div style={{ flex:1, fontFamily: T.sans, fontSize: 14, color: value ? (dark?'#fff':T.ink) : (dark?'rgba(255,255,255,0.5)':T.ink50) }}>{value || placeholder}</div>
      {trailing}
    </div>
  );
}

Object.assign(window, {
  Phone, AppBar, BottomNav, NavSpacer, Drawer, AuthBlock, SearchBar,
});
