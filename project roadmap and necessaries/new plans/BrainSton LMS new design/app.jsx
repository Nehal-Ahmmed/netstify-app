// app.jsx — wires every screen into a DesignCanvas + a clickable hero prototype

// ──────────────────────────────────────────────────────────────
// Hero prototype — one phone, navigable like the real app
// ──────────────────────────────────────────────────────────────
function HeroProto() {
  const [stack, setStack] = React.useState(['home']); // simple history
  const [drawer, setDrawer] = React.useState(false);
  const [sheet, setSheet] = React.useState(false);
  const screen = stack[stack.length-1];
  const go = (s) => setStack(st => [...st, s]);
  const back = () => setStack(st => st.length > 1 ? st.slice(0, -1) : st);

  const renderScreen = () => {
    switch (screen) {
      case 'home':       return <HomeScreen onOpenDrawer={()=>setDrawer(true)} onOpenCourse={()=>go('course')} onOpenCart={()=>go('cart')}/>;
      case 'courses':    return <CoursesScreen onOpenDrawer={()=>setDrawer(true)} onOpenList={()=>go('list')} onOpenCart={()=>go('cart')}/>;
      case 'list':       return <CourseListScreen onBack={back} onOpenCourse={()=>go('course')}/>;
      case 'course':     return <CourseDetailScreen onBack={back} onAddToCart={()=>go('cart')} onOpenSheet={()=>setSheet(true)}/>;
      case 'articles':   return <ArticlesScreen onOpenDrawer={()=>setDrawer(true)} onOpenArticle={()=>go('article')} onOpenCart={()=>go('cart')}/>;
      case 'article':    return <ArticleDetailScreen onBack={back}/>;
      case 'study':      return <StudyScreen onOpenDrawer={()=>setDrawer(true)} onOpenRoom={()=>go('room')} onOpenCart={()=>go('cart')}/>;
      case 'room':       return <StudyRoomScreen onBack={back} onOpenModule={()=>go('module')}/>;
      case 'module':     return <ModuleScreen onBack={back} onOpenVideo={()=>go('video')}/>;
      case 'video':      return <VideoScreen onBack={back}/>;
      case 'cart':       return <CartScreen onBack={back} onCheckout={()=>go('checkout')}/>;
      case 'checkout':   return <CheckoutScreen onBack={back}/>;
      case 'wishlist':   return <WishlistScreen onBack={back}/>;
      case 'profile':    return <ProfileScreen onBack={back} onOpenSettings={()=>go('settings')}/>;
      case 'settings':   return <SettingsScreen onBack={back}/>;
      case 'login':      return <LoginScreen onBack={back}/>;
      case 'signup':     return <SignupScreen onBack={back}/>;
      default:           return <HomeScreen onOpenDrawer={()=>setDrawer(true)}/>;
    }
  };

  // Map tabs to their root screens so the bottom-nav can switch
  const tabFor = (id) => ({ home:'home', courses:'courses', articles:'articles', study:'study' }[id]);

  // Intercept bottom-nav by injecting a click delegate
  const handleNavClick = (id) => setStack([tabFor(id)]);

  return (
    <div style={{ position:'relative' }}>
      {renderScreen()}
      {/* Override the inactive BottomNav present in screen via overlay */}
      {/* The screens already include a BottomNav with no handler — we put an interactive one on top */}
      {['home', 'courses', 'articles', 'study'].includes(screen) && (
        <div style={{ position:'absolute', left:0, right:0, bottom: 0, zIndex: 50, pointerEvents:'none' }}>
          <div style={{ pointerEvents:'auto' }}>
            <BottomNav active={screen} onChange={handleNavClick}/>
          </div>
        </div>
      )}
      {/* Drawer overlay */}
      <Drawer open={drawer} onClose={() => setDrawer(false)}/>
      {/* Preview sheet for Course Details */}
      {screen === 'course' && <PreviewSheet open={sheet} onClose={() => setSheet(false)}/>}
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
// Welcome / context note that sits at the top of the canvas
// ──────────────────────────────────────────────────────────────
function WelcomeNote() {
  return (
    <div style={{
      width: 760, padding: 28, background: '#FFFEF7', borderRadius: 16,
      border: '1px solid rgba(15,26,24,0.08)',
      boxShadow: '0 1px 0 rgba(15,26,24,0.04)',
      fontFamily: T.sans,
    }}>
      <div style={{ display:'flex', alignItems:'center', gap: 10, marginBottom: 14 }}>
        <BrandMark size={28} label/>
        <Chip tone="soft" style={{ marginLeft: 'auto' }}>v1 · Hi-fi mobile</Chip>
      </div>
      <div style={{ fontFamily: T.serif, fontSize: 34, color: T.ink, letterSpacing: -0.4, lineHeight: 1.05, marginBottom: 12 }}>
        BrainSton LMS — app screens
      </div>
      <div style={{ fontFamily: T.sans, fontSize: 14, color: T.ink70, lineHeight: 1.6, marginBottom: 16, maxWidth: 600 }}>
        Hi-fi mobile mockups for the MediShark tenant, following the 30-screen blueprint. The first artboard is a <b>clickable hero prototype</b> — tap around any tab; everything else routes. Drag the canvas, double-click to focus a single artboard.
      </div>
      <div style={{ display:'flex', flexWrap:'wrap', gap: 16, paddingTop: 14, borderTop: '1px solid rgba(15,26,24,0.06)' }}>
        {[
          { l: 'Aesthetic', v: 'Calm clinical · serif headlines' },
          { l: 'Primary', v: 'Deep teal #0E6E5A' },
          { l: 'Type', v: 'Inter Tight · Instrument Serif' },
          { l: 'Tenant', v: 'MediShark (Bangladesh)' },
        ].map((m, i) => (
          <div key={i}>
            <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1, color: T.ink50, textTransform:'uppercase' }}>{m.l}</div>
            <div style={{ fontFamily: T.sans, fontSize: 13, color: T.ink, marginTop: 4, fontWeight: 500 }}>{m.v}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

// ──────────────────────────────────────────────────────────────
// The whole canvas
// ──────────────────────────────────────────────────────────────
const W = 402, H = 874;

function App() {
  return (
    <DesignCanvas>
      <DCSection id="intro" title="Read me" subtitle="Aesthetic notes & how to use this">
        <DCArtboard id="welcome" label="Welcome" width={780} height={300}>
          <WelcomeNote/>
        </DCArtboard>
      </DCSection>

      <DCSection id="proto" title="Clickable hero prototype" subtitle="Tap anywhere. Bottom-nav switches tabs, taps route into details.">
        <DCArtboard id="hero" label="Live prototype" width={W} height={H}>
          <HeroProto/>
        </DCArtboard>
        <DCArtboard id="splash" label="Splash" width={W} height={H}>
          <SplashScreen/>
        </DCArtboard>
        <DCArtboard id="login-social" label="Login · social" width={W} height={H}>
          <LoginScreen mode="social"/>
        </DCArtboard>
        <DCArtboard id="login-form" label="Login · form" width={W} height={H}>
          <LoginScreen mode="form"/>
        </DCArtboard>
        <DCArtboard id="signup" label="Sign up" width={W} height={H}>
          <SignupScreen/>
        </DCArtboard>
        <DCArtboard id="otp" label="OTP verification" width={W} height={H}>
          <OtpScreen/>
        </DCArtboard>
      </DCSection>

      <DCSection id="marketing" title="Marketing & catalog" subtitle="Home, Courses tab, lists, course details, mentor, articles">
        <DCArtboard id="home" label="Home tab" width={W} height={H}>
          <HomeScreen/>
        </DCArtboard>
        <DCArtboard id="home-drawer" label="Home · drawer open" width={W} height={H}>
          <HomeWithDrawer/>
        </DCArtboard>
        <DCArtboard id="courses" label="Courses tab" width={W} height={H}>
          <CoursesScreen/>
        </DCArtboard>
        <DCArtboard id="course-list" label="Course list · filtered" width={W} height={H}>
          <CourseListScreen/>
        </DCArtboard>
        <DCArtboard id="course-details" label="Course details" width={W} height={H}>
          <CourseDetailScreen/>
        </DCArtboard>
        <DCArtboard id="course-sheet" label="Course · preview sheet" width={W} height={H}>
          <CourseWithSheet/>
        </DCArtboard>
        <DCArtboard id="articles" label="Articles tab" width={W} height={H}>
          <ArticlesScreen/>
        </DCArtboard>
        <DCArtboard id="article" label="Article detail" width={W} height={H}>
          <ArticleDetailScreen/>
        </DCArtboard>
      </DCSection>

      <DCSection id="study" title="Learning experience" subtitle="The core: Study tab, Study Room, Module, Video player">
        <DCArtboard id="study-tab" label="Study tab" width={W} height={H}>
          <StudyScreen/>
        </DCArtboard>
        <DCArtboard id="study-gate" label="Study · guest gate" width={W} height={H}>
          <StudyScreen authed={false}/>
        </DCArtboard>
        <DCArtboard id="study-room" label="Study Room" width={W} height={H}>
          <StudyRoomScreen/>
        </DCArtboard>
        <DCArtboard id="module" label="Module · video list" width={W} height={H}>
          <ModuleScreen/>
        </DCArtboard>
        <DCArtboard id="video" label="Video player" width={W} height={H}>
          <VideoScreen/>
        </DCArtboard>
      </DCSection>

      <DCSection id="commerce" title="Cart, checkout, wishlist" subtitle="Purchase flow + saved courses + empty states">
        <DCArtboard id="cart" label="Cart · populated" width={W} height={H}>
          <CartScreen/>
        </DCArtboard>
        <DCArtboard id="cart-empty" label="Cart · empty" width={W} height={H}>
          <CartScreen empty/>
        </DCArtboard>
        <DCArtboard id="cart-gate" label="Cart · guest gate" width={W} height={H}>
          <CartScreen authed={false}/>
        </DCArtboard>
        <DCArtboard id="checkout" label="Checkout" width={W} height={H}>
          <CheckoutScreen/>
        </DCArtboard>
        <DCArtboard id="wishlist" label="Wishlist" width={W} height={H}>
          <WishlistScreen/>
        </DCArtboard>
      </DCSection>

      <DCSection id="account" title="Account & settings" subtitle="Profile, settings, drawer, gates">
        <DCArtboard id="profile" label="Profile" width={W} height={H}>
          <ProfileScreen/>
        </DCArtboard>
        <DCArtboard id="profile-gate" label="Profile · guest gate" width={W} height={H}>
          <ProfileScreen authed={false}/>
        </DCArtboard>
        <DCArtboard id="settings" label="Settings" width={W} height={H}>
          <SettingsScreen/>
        </DCArtboard>
      </DCSection>
    </DesignCanvas>
  );
}

// Static helper: Home with drawer permanently open (for the canvas thumbnail)
function HomeWithDrawer() {
  return (
    <div style={{ position:'relative' }}>
      <HomeScreen/>
      <div style={{ position:'absolute', inset:0 }}>
        <Drawer open={true} onClose={()=>{}}/>
      </div>
    </div>
  );
}

function CourseWithSheet() {
  return (
    <div style={{ position:'relative' }}>
      <CourseDetailScreen/>
      <div style={{ position:'absolute', inset:0 }}>
        <PreviewSheet open={true} onClose={()=>{}}/>
      </div>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App/>);
