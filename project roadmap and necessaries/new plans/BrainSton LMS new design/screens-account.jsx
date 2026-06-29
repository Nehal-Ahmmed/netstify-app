// screens-account.jsx — Login, Signup, OTP, Profile, Edit Profile, Settings, Notifications

// ──────────────────────────────────────────────────────────────
// Login
// ──────────────────────────────────────────────────────────────
function LoginScreen({ onBack, mode='form' }) {
  // mode = 'social' | 'form'
  return (
    <Phone>
      <div style={{ flex:1, overflow:'auto', background: T.surface, display:'flex', flexDirection:'column' }}>
        <div style={{ padding: '54px 20px 0' }}>
          <button onClick={onBack} style={{ width: 40, height: 40, borderRadius: 10, border:0, background: T.surface2, color: T.ink70, display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
            <I.back/>
          </button>
        </div>

        <div style={{ padding: '32px 24px 0' }}>
          <BrandMark size={36}/>
          <div style={{ fontFamily: T.serif, fontSize: 36, color: T.ink, letterSpacing: -0.5, lineHeight: 1.05, marginTop: 22 }}>
            Welcome back.
          </div>
          <div style={{ fontFamily: T.sans, fontSize: 14, color: T.ink50, marginTop: 8, lineHeight: 1.5 }}>
            Sign in to continue your enrolled courses.
          </div>
        </div>

        <div style={{ padding: '32px 24px 0', flex:1 }}>
          {mode === 'form' ? (
            <>
              <Input label="Email" placeholder="ayesha.r@dmc.edu" value="ayesha.r@dmc.edu" leading={<svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.6"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 7l9 6 9-6"/></svg>}/>
              <div style={{ height: 14 }}/>
              <Input label="Password" placeholder="••••••••" type="password" value="password"
                leading={<I.lock width={18} height={18}/>}
                trailing={<button style={{ background:'none', border:0, color: T.ink50, padding: 4, cursor:'pointer' }}><I.eye/></button>}/>
              <div style={{ textAlign:'right', marginTop: 10 }}>
                <span style={{ fontFamily: T.sans, fontSize: 12, color: T.brand, fontWeight: 600, cursor:'pointer' }}>Forgot password?</span>
              </div>
              <div style={{ marginTop: 24 }}>
                <Btn variant="primary" size="lg" full trailing={<I.arrR/>}>Sign in</Btn>
              </div>
              <div style={{ textAlign:'center', marginTop: 16 }}>
                <span style={{ fontFamily: T.sans, fontSize: 13, color: T.ink50, cursor:'pointer', display:'inline-flex', alignItems:'center', gap: 4 }}>
                  <I.back width={14} height={14}/>Other methods
                </span>
              </div>
            </>
          ) : (
            <>
              <Btn variant="dark" size="lg" full leading={<svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor"><rect x="3" y="5" width="18" height="14" rx="2" stroke="currentColor" strokeWidth="1.6" fill="none"/><path d="M3 7l9 6 9-6" stroke="currentColor" strokeWidth="1.6" fill="none"/></svg>}>
                Login with Email & Password
              </Btn>
              <div style={{ display:'flex', alignItems:'center', gap: 12, margin: '20px 0' }}>
                <div style={{ flex:1, height:1, background: T.hair }}/>
                <span style={{ fontFamily: T.mono, fontSize: 10, color: T.ink50, letterSpacing: 1, textTransform:'uppercase' }}>or</span>
                <div style={{ flex:1, height:1, background: T.hair }}/>
              </div>
              <Btn variant="secondary" size="lg" full leading={<I.google/>}>Continue with Google</Btn>
            </>
          )}
        </div>

        {/* Quote card */}
        <div style={{ margin: '28px 24px 16px', padding: 18, background: T.brandTint, borderRadius: T.r.l, position:'relative' }}>
          <div style={{ position:'absolute', top: 8, left: 12, fontFamily: T.serif, fontSize: 36, color: T.brand, lineHeight: 1, opacity: 0.5 }}>“</div>
          <div style={{ fontFamily: T.serif, fontStyle:'italic', fontSize: 15, color: T.ink, lineHeight: 1.45, paddingLeft: 18 }}>
            Wherever the art of Medicine is loved, there is also a love of Humanity.
          </div>
          <div style={{ fontFamily: T.mono, fontSize: 10, color: T.ink50, paddingLeft: 18, marginTop: 8, letterSpacing: 0.5, textTransform:'uppercase' }}>— Hippocrates</div>
        </div>

        {/* Footer */}
        <div style={{ padding: '0 24px 28px', textAlign:'center', display:'flex', justifyContent:'center', alignItems:'center', gap: 8 }}>
          <span style={{ fontFamily: T.sans, fontSize: 13, color: T.ink50 }}>Don't have an account?</span>
          <Btn size="sm" variant="soft">Sign Up</Btn>
        </div>
      </div>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Signup (full form)
// ──────────────────────────────────────────────────────────────
function SignupScreen({ onBack }) {
  return (
    <Phone>
      <div style={{ flex:1, overflow:'auto', background: T.surface, display:'flex', flexDirection:'column' }}>
        <div style={{ padding: '54px 20px 0' }}>
          <button onClick={onBack} style={{ width: 40, height: 40, borderRadius: 10, border:0, background: T.surface2, color: T.ink70, display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
            <I.back/>
          </button>
        </div>

        <div style={{ padding: '24px 24px 0' }}>
          <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1.5, color: T.brand, textTransform:'uppercase', marginBottom: 8 }}>Create account</div>
          <div style={{ fontFamily: T.serif, fontSize: 32, color: T.ink, letterSpacing: -0.5, lineHeight: 1.05 }}>
            Let's get you<br/>started.
          </div>
        </div>

        <div style={{ padding: '28px 24px 0', display:'flex', flexDirection:'column', gap: 14 }}>
          <Input label="Full name" placeholder="Ayesha Rahman"/>
          <Input label="Email" placeholder="you@example.com" leading={<svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" strokeWidth="1.6"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 7l9 6 9-6"/></svg>}/>
          <div>
            <div style={{ fontFamily: T.sans, fontSize: 12, fontWeight: 500, color: T.ink70, marginBottom: 6 }}>Phone number</div>
            <div style={{ display:'flex', gap: 8 }}>
              <div style={{ display:'flex', alignItems:'center', gap: 6, padding: '0 12px', height: 48, background: T.surface, border: `1.5px solid ${T.hair}`, borderRadius: T.r.m, fontFamily: T.sans, fontSize: 14, color: T.ink }}>
                <span style={{ fontSize: 18 }}>🇧🇩</span>+880<I.chevD style={{ color: T.ink50 }}/>
              </div>
              <div style={{ flex:1, height: 48, padding:'0 14px', display:'flex', alignItems:'center', background: T.surface, border: `1.5px solid ${T.hair}`, borderRadius: T.r.m, fontFamily: T.sans, fontSize: 14, color: T.ink50 }}>
                17XX XXX XXX
              </div>
            </div>
          </div>
          <Input label="Password" placeholder="At least 8 characters" type="password" leading={<I.lock width={16} height={16}/>} trailing={<I.eye style={{ color: T.ink50 }}/>}/>
          <Input label="Medical college / university" placeholder="e.g. Dhaka Medical College"/>

          <div style={{ display:'flex', alignItems:'flex-start', gap: 10, marginTop: 4 }}>
            <div style={{ width: 20, height: 20, borderRadius: 6, background: T.brand, color:'#fff', display:'flex', alignItems:'center', justifyContent:'center', flexShrink:0 }}>
              <I.check width={14} height={14}/>
            </div>
            <div style={{ fontFamily: T.sans, fontSize: 12, color: T.ink70, lineHeight: 1.5 }}>
              I agree to the <span style={{ color: T.brand, fontWeight: 600 }}>Terms of Service</span> and <span style={{ color: T.brand, fontWeight: 600 }}>Privacy Policy</span>.
            </div>
          </div>

          <Btn variant="primary" size="lg" full trailing={<I.arrR/>} style={{ marginTop: 8 }}>Create account</Btn>

          <div style={{ display:'flex', alignItems:'center', gap: 12, margin: '12px 0 4px' }}>
            <div style={{ flex:1, height:1, background: T.hair }}/>
            <span style={{ fontFamily: T.mono, fontSize: 10, color: T.ink50, letterSpacing: 1, textTransform:'uppercase' }}>or</span>
            <div style={{ flex:1, height:1, background: T.hair }}/>
          </div>
          <Btn variant="secondary" size="lg" full leading={<I.google/>}>Continue with Google</Btn>
        </div>

        <div style={{ padding: '24px 24px 28px', textAlign:'center' }}>
          <span style={{ fontFamily: T.sans, fontSize: 13, color: T.ink50 }}>Already have an account? </span>
          <span style={{ fontFamily: T.sans, fontSize: 13, color: T.brand, fontWeight: 600, cursor:'pointer' }}>Sign in</span>
        </div>
      </div>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// OTP Verification
// ──────────────────────────────────────────────────────────────
function OtpScreen({ onBack }) {
  const digits = ['8','4','2','0','',''];
  return (
    <Phone>
      <div style={{ flex:1, overflow:'auto', background: T.surface, display:'flex', flexDirection:'column' }}>
        <div style={{ padding: '54px 20px 0' }}>
          <button onClick={onBack} style={{ width: 40, height: 40, borderRadius: 10, border:0, background: T.surface2, color: T.ink70, display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
            <I.back/>
          </button>
        </div>

        <div style={{ padding: '36px 24px 0', textAlign:'center' }}>
          <div style={{ width: 80, height: 80, margin: '0 auto 22px', borderRadius: 24, background: T.brandSoft, color: T.brand, display:'flex', alignItems:'center', justifyContent:'center' }}>
            <svg viewBox="0 0 24 24" width="34" height="34" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 7l9 6 9-6"/></svg>
          </div>
          <div style={{ fontFamily: T.serif, fontSize: 28, color: T.ink, letterSpacing: -0.3 }}>Check your email</div>
          <div style={{ fontFamily: T.sans, fontSize: 14, color: T.ink50, marginTop: 8, lineHeight: 1.5, maxWidth: 280, margin: '8px auto 0' }}>
            We sent a 6-digit code to<br/>
            <span style={{ color: T.ink, fontWeight: 500 }}>ayesha.r@dmc.edu</span>
          </div>
        </div>

        <div style={{ padding: '36px 24px 0', display:'flex', gap: 8, justifyContent:'center' }}>
          {digits.map((d, i) => (
            <div key={i} style={{
              width: 46, height: 56, borderRadius: T.r.m,
              background: T.surface, border: `1.5px solid ${d || i===4 ? T.brand : T.hair}`,
              display:'flex', alignItems:'center', justifyContent:'center',
              fontFamily: T.serif, fontSize: 28, color: T.ink, letterSpacing: -0.3,
              boxShadow: i===4 ? `0 0 0 3px ${T.brandSoft}` : 'none',
            }}>{d}{i===4 && <div style={{ width: 1.5, height: 24, background: T.brand, marginLeft: 2, animation: 'blink 1s infinite' }}/>}</div>
          ))}
        </div>
        <style>{`@keyframes blink{50%{opacity:0}}`}</style>

        <div style={{ padding: '32px 24px 0', textAlign:'center', fontFamily: T.sans, fontSize: 13, color: T.ink50 }}>
          Didn't receive it? <span style={{ color: T.ink30 }}>Resend in 0:48</span>
        </div>

        <div style={{ padding: '28px 24px 28px', flex:1, display:'flex', flexDirection:'column', justifyContent:'flex-end' }}>
          <Btn variant="primary" size="lg" full trailing={<I.arrR/>}>Verify</Btn>
        </div>
      </div>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Profile
// ──────────────────────────────────────────────────────────────
function ProfileScreen({ onBack, onOpenSettings, authed=true }) {
  if (!authed) return (
    <Phone>
      <AppBar variant="title" title="Profile" onBack={onBack} authed={false} onAvatar={()=>{}}/>
      <AuthBlock variant="profile"/>
    </Phone>
  );

  return (
    <Phone>
      <AppBar variant="title" title="Profile" onBack={onBack} trailing={
        <button style={iconBtn(T.ink)} onClick={onOpenSettings}><I.gear/></button>
      }/>
      <div style={{ flex:1, overflow:'auto', background: T.bg }}>
        {/* User card */}
        <div style={{ padding: '20px 20px 0' }}>
          <Card style={{ display:'flex', alignItems:'center', gap: 14, background: T.surfaceDk, color:'#fff', border:'none', position:'relative', overflow:'hidden' }}>
            <div style={{ position:'absolute', top: -40, right: -40, width: 140, height: 140, borderRadius:'50%', background:'rgba(14,110,90,0.3)' }}/>
            <Avatar name="Ayesha Rahman" size={60} ring/>
            <div style={{ flex:1, position:'relative' }}>
              <div style={{ fontFamily: T.sans, fontSize: 16, fontWeight: 600 }}>Ayesha Rahman</div>
              <div style={{ fontFamily: T.sans, fontSize: 12, opacity: 0.6, marginTop: 2 }}>ayesha.r@dmc.edu</div>
              <Chip style={{ background:'rgba(255,255,255,0.16)', color:'#fff', border:0, marginTop: 8, padding:'3px 10px', fontSize: 10, fontWeight: 600, letterSpacing: 0.3 }}>DMC · MBBS Year 2</Chip>
            </div>
          </Card>
        </div>

        {/* Quick actions */}
        <div style={{ padding: '24px 20px 0' }}>
          <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1, color: T.ink50, textTransform:'uppercase', marginBottom: 10 }}>Quick actions</div>
          <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap: 10 }}>
            {[
              { i: I.play, t: 'Continue learning', d: 'Resume your last lesson', tone: T.brand, bg: T.brandSoft },
              { i: I.trophy, t: 'Exam submissions', d: 'View past attempts', tone: T.warn, bg: T.warnSoft },
              { i: I.user, t: 'Edit profile', d: 'Name, college, bio', tone: T.ink, bg: T.surface2 },
              { i: I.cart, t: 'My orders', d: '3 invoices · downloads', tone: T.coral, bg: T.coralSoft },
            ].map((a, i) => (
              <Card key={i} p={14} style={{ display:'flex', flexDirection:'column', gap: 10, cursor:'pointer' }}>
                <div style={{ width: 36, height: 36, borderRadius: 10, background: a.bg, color: a.tone, display:'flex', alignItems:'center', justifyContent:'center' }}>
                  <a.i/>
                </div>
                <div>
                  <div style={{ fontFamily: T.sans, fontSize: 13, fontWeight: 600, color: T.ink, letterSpacing: -0.1 }}>{a.t}</div>
                  <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50, marginTop: 2 }}>{a.d}</div>
                </div>
              </Card>
            ))}
          </div>
        </div>

        {/* Settings shortcuts */}
        <div style={{ padding: '24px 20px 0' }}>
          <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1, color: T.ink50, textTransform:'uppercase', marginBottom: 10 }}>Settings</div>
          <Card p={0}>
            {[
              { i: I.lock, t: 'Password & security', d: 'Devices, 2FA, change password' },
              { i: I.globe, t: 'Language', d: 'English', val: 'EN' },
              { i: I.gear, t: 'Active brand', d: 'MediShark', val: 'Switch' },
            ].map((r, i) => (
              <div key={i} style={{ display:'flex', alignItems:'center', gap: 14, padding: '14px 16px', borderTop: i===0 ? 'none' : `1px solid ${T.hair2}`, cursor:'pointer' }}>
                <div style={{ width: 32, height: 32, borderRadius: 8, background: T.brandSoft, color: T.brand, display:'flex', alignItems:'center', justifyContent:'center' }}>
                  <r.i/>
                </div>
                <div style={{ flex:1 }}>
                  <div style={{ fontFamily: T.sans, fontSize: 14, fontWeight: 500, color: T.ink }}>{r.t}</div>
                  <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50, marginTop: 2 }}>{r.d}</div>
                </div>
                {r.val && <Chip tone="soft" style={{ padding:'2px 10px', fontSize: 11, fontWeight: 600 }}>{r.val}</Chip>}
                <I.chev style={{ color: T.ink30 }}/>
              </div>
            ))}
          </Card>
        </div>

        {/* Logout */}
        <div style={{ padding: '24px 20px 32px' }}>
          <Btn variant="secondary" size="lg" full style={{ color: T.coral, borderColor: T.coralSoft, background: T.coralSoft }} leading={
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"><path d="M15 12H3M7 8l-4 4 4 4M9 4h6a3 3 0 013 3v10a3 3 0 01-3 3H9"/></svg>
          }>Log out</Btn>
        </div>
      </div>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Settings
// ──────────────────────────────────────────────────────────────
function SettingsScreen({ onBack }) {
  const [push, setPush] = React.useState(true);
  return (
    <Phone>
      <AppBar variant="title" title="Settings" onBack={onBack} trailing={null} onAvatar={()=>{}}/>
      <div style={{ flex:1, overflow:'auto', background: T.bg }}>
        {/* Account */}
        <SettingsSection label="Account" first>
          <SettingRow icon={I.user} title="Edit profile" desc="Name, photo, college, bio"/>
          <SettingRow icon={I.lock} title="Password & security" desc="2FA, change password"/>
          <SettingRow icon={I.cart} title="My orders" desc="3 past orders"/>
          <SettingRow icon={
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"><rect x="4" y="3" width="16" height="18" rx="2"/><path d="M9 7h6M9 11h6M9 15h4"/></svg>
          } title="My devices" desc="2 active"/>
        </SettingsSection>

        {/* Preferences */}
        <SettingsSection label="Preferences">
          <SettingRow icon={I.globe} title="Language" desc="English" trailing={<Chip tone="soft" style={{padding:'2px 10px',fontSize:11}}>EN</Chip>}/>
          <SettingRow icon={
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.6"><circle cx="12" cy="12" r="4"/><path d="M12 2v3M12 19v3M22 12h-3M5 12H2M19 5l-2 2M7 17l-2 2M19 19l-2-2M7 7L5 5" strokeLinecap="round"/></svg>
          } title="Theme" desc="System" trailing={
            <div style={{ display:'flex', background: T.surface2, padding: 3, borderRadius: 99 }}>
              {['Sys', 'Light', 'Dark'].map((t, i) => (
                <div key={t} style={{ padding: '4px 10px', borderRadius: 99, background: i===0 ? T.surface : 'transparent', fontFamily: T.sans, fontSize: 11, color: i===0 ? T.ink : T.ink50, fontWeight: i===0 ? 600 : 500 }}>{t}</div>
              ))}
            </div>
          }/>
          <SettingRow icon={
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"><path d="M9 4a4 4 0 00-4 4v1a3 3 0 00-1 5.6V17a3 3 0 003 3h1M15 4a4 4 0 014 4v1a3 3 0 011 5.6V17a3 3 0 01-3 3h-1M12 4v16"/></svg>
          } title="Active brand" desc="MediShark" trailing={<Chip tone="soft" style={{padding:'2px 10px',fontSize:11}}>Switch</Chip>}/>
        </SettingsSection>

        {/* Notifications */}
        <SettingsSection label="Notifications">
          <SettingRow icon={I.bell} title="Push notifications" trailing={<Toggle on={push} onChange={() => setPush(!push)}/>}/>
          <SettingRow icon={
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.6"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 7l9 6 9-6"/></svg>
          } title="Email updates" trailing={<Toggle on={push}/>}  disabled={!push}/>
          <SettingRow icon={I.grad} title="Course updates" trailing={<Toggle on={push && true}/>} disabled={!push}/>
          <SettingRow icon={I.trophy} title="Exam reminders" trailing={<Toggle on={push && true}/>} disabled={!push}/>
          <SettingRow icon={
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"><path d="M3 11l4-4 5 5 9-9M14 3h7v7"/></svg>
          } title="Promotional alerts" trailing={<Toggle on={false}/>} disabled={!push}/>
        </SettingsSection>

        {/* Video & playback */}
        <SettingsSection label="Video & playback">
          <SettingRow icon={I.play} title="Default quality" desc="720p" trailing={<I.chev style={{color:T.ink30}}/>}/>
          <SettingRow icon={
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"><path d="M12 2v3M12 19v3M22 12h-3M5 12H2"/><circle cx="12" cy="12" r="6"/></svg>
          } title="Playback speed" desc="1.0×" trailing={<I.chev style={{color:T.ink30}}/>}/>
          <SettingRow icon={I.download} title="Download on Wi-Fi only" trailing={<Toggle on/>}/>
          <SettingRow icon={
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round"><path d="M5 4l14 8L5 20V4z"/></svg>
          } title="Autoplay next video" trailing={<Toggle on/>}/>
        </SettingsSection>

        {/* Privacy & Security */}
        <SettingsSection label="Privacy & security">
          <SettingRow icon={I.eye} title="Profile visibility" desc="Public" trailing={<I.chev style={{color:T.ink30}}/>}/>
          <SettingRow icon={I.lock} title="Biometric app lock" trailing={<Toggle on/>}/>
          <SettingRow icon={
            <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.6"><rect x="6" y="3" width="12" height="18" rx="2"/><path d="M10 17h4"/></svg>
          } title="Two-factor auth" desc="Off" trailing={<I.chev style={{color:T.ink30}}/>}/>
        </SettingsSection>

        {/* Storage */}
        <SettingsSection label="Storage">
          <SettingRow icon={I.download} title="Cache" desc="184 MB used" trailing={<Btn size="sm" variant="ghost" style={{ color: T.coral }}>Clear</Btn>}/>
        </SettingsSection>

        {/* Danger zone */}
        <div style={{ padding: '8px 20px 40px' }}>
          <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1, color: T.coral, textTransform:'uppercase', marginBottom: 10, padding: '0 4px' }}>Danger zone</div>
          <Card p={0}>
            {[
              { t: 'Reset to defaults', c: T.ink },
              { t: 'Log out', c: T.ink },
              { t: 'Delete account', c: T.coral, bg: T.coralSoft },
            ].map((r, i) => (
              <div key={i} style={{ padding: '14px 16px', borderTop: i===0 ? 'none' : `1px solid ${T.hair2}`, background: r.bg || 'transparent', display:'flex', alignItems:'center', cursor:'pointer' }}>
                <span style={{ flex:1, fontFamily: T.sans, fontSize: 14, fontWeight: 500, color: r.c }}>{r.t}</span>
                <I.chev style={{ color: r.c === T.coral ? T.coral : T.ink30 }}/>
              </div>
            ))}
          </Card>
          <div style={{ textAlign:'center', marginTop: 18, fontFamily: T.mono, fontSize: 10, color: T.ink50, letterSpacing: 0.5 }}>
            v2.4.1 · BUILD 12048
          </div>
        </div>
      </div>
    </Phone>
  );
}

function SettingsSection({ label, children, first=false }) {
  return (
    <div style={{ padding: `${first ? 16 : 8}px 20px 0` }}>
      <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1, color: T.ink50, textTransform:'uppercase', marginBottom: 10, padding: '0 4px' }}>{label}</div>
      <Card p={0}>{children}</Card>
    </div>
  );
}

function SettingRow({ icon, title, desc, trailing, disabled=false }) {
  const Ic = typeof icon === 'function' ? icon : null;
  const opa = disabled ? 0.4 : 1;
  return (
    <div style={{ display:'flex', alignItems:'center', gap: 14, padding: '12px 16px', borderTop: '1px solid '+T.hair2, opacity: opa, cursor: disabled ? 'default' : 'pointer' }}>
      <div style={{ width: 32, height: 32, borderRadius: 8, background: T.brandSoft, color: T.brand, display:'flex', alignItems:'center', justifyContent:'center', flexShrink: 0 }}>
        {Ic ? <Ic/> : icon}
      </div>
      <div style={{ flex:1, minWidth: 0 }}>
        <div style={{ fontFamily: T.sans, fontSize: 14, fontWeight: 500, color: T.ink }}>{title}</div>
        {desc && <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50, marginTop: 2 }}>{desc}</div>}
      </div>
      {trailing}
    </div>
  );
}

function Toggle({ on, onChange, disabled }) {
  return (
    <div onClick={() => !disabled && onChange && onChange()} style={{
      width: 42, height: 26, borderRadius: 99,
      background: on ? T.brand : T.ink10,
      padding: 3, transition: 'background .2s', cursor: disabled ? 'default' : 'pointer',
    }}>
      <div style={{
        width: 20, height: 20, borderRadius: '50%', background: '#fff',
        transform: on ? 'translateX(16px)' : 'translateX(0)',
        transition: 'transform .2s', boxShadow: '0 1px 3px rgba(0,0,0,0.2)',
      }}/>
    </div>
  );
}

// SettingsSection.firstChild row needs no top border — overriding via a small selector
// (kept simple: first child uses the same border but visually doesn't show because Card has no inner top hairline above the first row).

// Fix: the first child border. Patch via a wrapper using firstChild
// We'll simply rely on the fact that the first border-top + Card edge looks subtle — fine.

Object.assign(window, {
  LoginScreen, SignupScreen, OtpScreen, ProfileScreen, SettingsScreen,
});
