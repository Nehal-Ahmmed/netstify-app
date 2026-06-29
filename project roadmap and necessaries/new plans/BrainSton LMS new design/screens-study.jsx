// screens-study.jsx — Study tab, Study Room, Module Details, Video Player

function StudyScreen({ onOpenDrawer, onOpenRoom, onOpenCart, authed=true }) {
  if (!authed) return (
    <Phone>
      <AppBar variant="default" onMenu={onOpenDrawer} onAvatar={()=>{}} authed={false}/>
      <AuthBlock variant="study"/>
      <BottomNav active="study"/>
    </Phone>
  );

  const courses = [
    { t: 'Anatomy of the Upper Limb', m: 'Dr. Tanvir Hossain', prog: 0.62, status: 'In progress', left: '11 months left', tone: 'teal' },
    { t: 'Biochem: Carbohydrate Metabolism', m: 'Dr. Nasrin Akter', prog: 0.18, status: 'New', left: '1 year left', tone: 'sand' },
    { t: 'Physiology: Cardiovascular System', m: 'Dr. Sumi Begum', prog: 1.0, status: 'Completed', left: '2 years left', tone: 'cool' },
  ];

  return (
    <Phone>
      <AppBar variant="default" onMenu={onOpenDrawer} onSearch={()=>{}} onCart={onOpenCart} cartCount={2} onAvatar={()=>{}}/>
      <div style={{ flex:1, overflow:'auto', background: T.bg }}>
        {/* Greeting */}
        <div style={{ padding: '20px 20px 16px', display:'flex', alignItems:'center', gap: 12 }}>
          <Avatar name="Ayesha Rahman" size={44}/>
          <div>
            <div style={{ fontFamily: T.sans, fontSize: 12, color: T.ink50 }}>Good morning</div>
            <div style={{ fontFamily: T.serif, fontSize: 22, color: T.ink, letterSpacing: -0.3, lineHeight: 1.1 }}>Ayesha 👋</div>
          </div>
        </div>

        {/* Progress card */}
        <div style={{ padding: '0 20px 20px' }}>
          <Card style={{ background: T.surfaceDk, color: '#fff', position:'relative', overflow:'hidden' }}>
            <div style={{ position:'absolute', top: -40, right: -40, width: 160, height: 160, borderRadius:'50%', background: 'rgba(14,110,90,0.35)' }}/>
            <div style={{ position:'relative' }}>
              <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1.2, opacity: 0.6, textTransform:'uppercase', marginBottom: 6 }}>Overall progress</div>
              <div style={{ display:'flex', alignItems:'baseline', gap: 6, marginBottom: 14 }}>
                <div style={{ fontFamily: T.serif, fontSize: 44, letterSpacing: -0.5, lineHeight: 1 }}>47</div>
                <div style={{ fontFamily: T.sans, fontSize: 16, opacity: 0.8 }}>%</div>
              </div>
              <Progress value={0.47} color="#fff" track="rgba(255,255,255,0.18)" h={6}/>
              <div style={{ display:'flex', justifyContent:'space-between', marginTop: 14, fontFamily: T.sans, fontSize: 12, opacity: 0.85 }}>
                <span>62 / 132 videos</span>
                <span>1 / 3 courses</span>
              </div>
            </div>
          </Card>
        </div>

        {/* Tabs */}
        <div style={{ padding: '0 20px 14px' }}>
          <TabPill tabs={['Courses', 'eBooks', 'Submissions']} active={0}/>
        </div>

        {/* Search + filter */}
        <div style={{ padding: '0 20px 12px' }}>
          <SearchBar placeholder="Search your library…"/>
        </div>
        <div style={{ display:'flex', gap: 8, padding: '0 20px 16px', overflowX:'auto' }}>
          <Chip active>All</Chip>
          <Chip>Courses</Chip>
          <Chip>Test Series</Chip>
        </div>

        {/* Enrolled list */}
        <div style={{ padding: '0 20px' }}>
          {courses.map((c, i) => (
            <Card key={i} p={0} style={{ marginBottom: 14, overflow:'hidden' }} onClick={i===0 ? onOpenRoom : undefined}>
              <div style={{ display:'flex' }}>
                <Placeholder w={104} h={104} tone={c.tone} label="thumb" radius={0}/>
                <div style={{ flex:1, padding: 14, display:'flex', flexDirection:'column', justifyContent:'space-between' }}>
                  <div>
                    <div style={{ display:'flex', alignItems:'flex-start', justifyContent:'space-between', gap: 8 }}>
                      <div style={{ fontFamily: T.sans, fontSize: 14, fontWeight: 600, color: T.ink, lineHeight: 1.3, letterSpacing: -0.1 }}>{c.t}</div>
                      <Chip tone={c.prog===1 ? 'ok' : c.prog<0.2 ? 'coral' : 'soft'} style={{ padding:'2px 8px', fontSize: 10, fontWeight: 700, letterSpacing: 0.3 }}>
                        {c.status}
                      </Chip>
                    </div>
                    <div style={{ display:'flex', alignItems:'center', gap: 6, marginTop: 6 }}>
                      <Avatar name={c.m} size={16}/>
                      <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50 }}>{c.m}</div>
                    </div>
                  </div>
                  <div>
                    <Progress value={c.prog} h={4}/>
                    <div style={{ display:'flex', alignItems:'center', justifyContent:'space-between', marginTop: 6 }}>
                      <div style={{ fontFamily: T.mono, fontSize: 10, color: T.ink50, letterSpacing: 0.3 }}>{Math.round(c.prog*100)}% complete</div>
                      <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50 }}>{c.left}</div>
                    </div>
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </div>

        <NavSpacer/>
      </div>
      <BottomNav active="study"/>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Study Room
// ──────────────────────────────────────────────────────────────
function StudyRoomScreen({ onBack, onOpenModule }) {
  return (
    <Phone>
      <div style={{ flex:1, overflow:'auto', background: T.bg, position:'relative' }}>
        {/* Header */}
        <div style={{
          background: T.brand, color: '#fff',
          padding: '54px 20px 24px', borderRadius: '0 0 28px 28px',
          position:'relative', overflow:'hidden',
        }}>
          <div style={{ position:'absolute', top: -30, right: -30, width: 180, height: 180, borderRadius:'50%', background:'rgba(255,255,255,0.06)' }}/>
          <div style={{ position:'absolute', bottom: -50, left: -30, width: 140, height: 140, borderRadius:'50%', background:'rgba(255,255,255,0.04)' }}/>

          <div style={{ position:'relative' }}>
            <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom: 18 }}>
              <button onClick={onBack} style={{ width:38, height:38, borderRadius: 10, background:'rgba(255,255,255,0.14)', border:0, color:'#fff', display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
                <I.back/>
              </button>
              <button style={{ position:'relative', width:38, height:38, borderRadius: 10, background:'rgba(255,255,255,0.14)', border:0, color:'#fff', display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
                <I.bell/>
                <span style={{ position:'absolute', top: 7, right: 8, width: 8, height: 8, borderRadius:'50%', background: T.coral, border:'2px solid '+T.brand }}/>
              </button>
            </div>

            <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1.2, opacity: 0.7, textTransform:'uppercase' }}>Study room</div>
            <div style={{ fontFamily: T.serif, fontSize: 26, letterSpacing: -0.3, lineHeight: 1.15, marginTop: 6, marginBottom: 16 }}>Anatomy of the<br/>Upper Limb</div>

            <div style={{ display:'flex', gap: 8, overflowX:'auto' }}>
              {[
                { l: 'Guideline', i: I.doc },
                { l: 'Exams', i: I.trophy },
                { l: 'Community', i: I.user },
                { l: 'Feedback', i: I.send },
              ].map((c, i) => (
                <button key={i} style={{
                  display:'flex', alignItems:'center', gap: 6, flexShrink:0,
                  padding: '8px 12px', borderRadius: T.r.pill, border:0,
                  background: 'rgba(255,255,255,0.14)', color: '#fff',
                  fontFamily: T.sans, fontSize: 12, fontWeight: 500, cursor:'pointer',
                }}>
                  <c.i width={14} height={14}/>{c.l}
                </button>
              ))}
            </div>

            <div style={{ display:'flex', gap: 24, marginTop: 22, paddingTop: 18, borderTop: '1px solid rgba(255,255,255,0.15)' }}>
              <div>
                <div style={{ fontFamily: T.serif, fontSize: 20, letterSpacing: -0.3 }}>3</div>
                <div style={{ fontFamily: T.mono, fontSize: 10, opacity: 0.7, letterSpacing: 0.5, textTransform:'uppercase' }}>Modules</div>
              </div>
              <div>
                <div style={{ fontFamily: T.serif, fontSize: 20, letterSpacing: -0.3 }}>28</div>
                <div style={{ fontFamily: T.mono, fontSize: 10, opacity: 0.7, letterSpacing: 0.5, textTransform:'uppercase' }}>Videos</div>
              </div>
              <div>
                <div style={{ fontFamily: T.serif, fontSize: 20, letterSpacing: -0.3 }}>17</div>
                <div style={{ fontFamily: T.mono, fontSize: 10, opacity: 0.7, letterSpacing: 0.5, textTransform:'uppercase' }}>Done</div>
              </div>
            </div>
          </div>
        </div>

        {/* Body */}
        <div style={{ padding: '20px 20px 0' }}>
          <SearchBar placeholder="Search modules…"/>
        </div>

        <div style={{ padding: '16px 20px 0' }}>
          <Card style={{ display:'flex', alignItems:'center', gap: 12, background: T.brandTint, border:'none' }}>
            <div style={{ width: 40, height: 40, borderRadius: 10, background: T.brand, color:'#fff', display:'flex', alignItems:'center', justifyContent:'center' }}>
              <I.doc/>
            </div>
            <div style={{ flex:1 }}>
              <div style={{ fontFamily: T.sans, fontSize: 14, fontWeight: 600, color: T.ink }}>Course Guideline</div>
              <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50, marginTop: 2 }}>How to study this course · 5 min read</div>
            </div>
            <div style={{ fontFamily: T.sans, fontSize: 12, color: T.brand, fontWeight: 600 }}>See more</div>
          </Card>
        </div>

        <div style={{ padding: '24px 20px 0' }}>
          <SectionHead kicker="Modules" title="Continue learning"/>
          {[
            { n: 'Bones of the Upper Limb', v: 7, dur: '1h 48m', done: 7, prog: 1.0, tone: 'teal' },
            { n: 'Muscle Compartments',     v: 9, dur: '2h 14m', done: 6, prog: 0.66, tone: 'sand' },
            { n: 'Vasculature & Nerves',    v: 8, dur: '2h 18m', done: 0, prog: 0, tone: 'cool' },
          ].map((m, i) => (
            <Card key={i} p={0} style={{ marginBottom: 12, overflow:'hidden' }} onClick={i===1 ? onOpenModule : undefined}>
              <div style={{ display:'flex' }}>
                <div style={{ position:'relative' }}>
                  <Placeholder w={104} h={104} tone={m.tone} label="" radius={0}/>
                  <div style={{ position:'absolute', inset:0, display:'flex', alignItems:'center', justifyContent:'center' }}>
                    <div style={{ width: 40, height: 40, borderRadius:'50%', background:'rgba(255,255,255,0.94)', color: T.brand, display:'flex', alignItems:'center', justifyContent:'center' }}>
                      <I.play/>
                    </div>
                  </div>
                  {m.prog===1 && (
                    <div style={{ position:'absolute', top: 8, right: 8, width: 22, height: 22, borderRadius:'50%', background: T.ok, color:'#fff', display:'flex', alignItems:'center', justifyContent:'center' }}>
                      <I.check width={12} height={12}/>
                    </div>
                  )}
                </div>
                <div style={{ flex:1, padding: 14 }}>
                  <div style={{ fontFamily: T.mono, fontSize: 10, color: T.ink50, letterSpacing: 0.5, textTransform:'uppercase' }}>Module {String(i+1).padStart(2,'0')}</div>
                  <div style={{ fontFamily: T.sans, fontSize: 14, fontWeight: 600, color: T.ink, marginTop: 4, letterSpacing: -0.1 }}>{m.n}</div>
                  <div style={{ display:'flex', gap: 10, marginTop: 6, marginBottom: 10 }}>
                    <div style={{ display:'flex', alignItems:'center', gap: 4, fontFamily: T.sans, fontSize: 11, color: T.ink50 }}>
                      <I.play width={12} height={12}/>{m.v} videos
                    </div>
                    <div style={{ display:'flex', alignItems:'center', gap: 4, fontFamily: T.sans, fontSize: 11, color: T.ink50 }}>
                      <I.clock/>{m.dur}
                    </div>
                  </div>
                  <Progress value={m.prog} h={4} color={m.prog===1 ? T.ok : T.brand}/>
                  <div style={{ fontFamily: T.mono, fontSize: 10, color: T.ink50, letterSpacing: 0.3, marginTop: 4 }}>{m.done}/{m.v} done</div>
                </div>
              </div>
            </Card>
          ))}
        </div>

        {/* Feedback CTA */}
        <div style={{ padding: '20px 20px 28px' }}>
          <Card style={{ background: T.coralSoft, border: 'none', display:'flex', alignItems:'center', gap: 12 }}>
            <div style={{ width: 40, height: 40, borderRadius: 10, background: T.coral, color:'#fff', display:'flex', alignItems:'center', justifyContent:'center' }}>
              <I.send/>
            </div>
            <div style={{ flex:1 }}>
              <div style={{ fontFamily: T.sans, fontSize: 13, fontWeight: 600, color: T.ink }}>How's it going?</div>
              <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink70, marginTop: 2 }}>Tell us what you'd improve.</div>
            </div>
            <Btn size="sm" variant="primary" style={{ background: T.coral }}>Feedback</Btn>
          </Card>
        </div>
      </div>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Module Details (video list)
// ──────────────────────────────────────────────────────────────
function ModuleScreen({ onBack, onOpenVideo }) {
  const vids = [
    { n: 'Origin & Insertion: Biceps', dur: '12:08', done: true },
    { n: 'Triceps mechanics', dur: '14:22', done: true },
    { n: 'Anterior forearm compartment', dur: '18:40', done: true },
    { n: 'Posterior forearm compartment', dur: '16:18', done: true },
    { n: 'Intrinsic hand muscles I', dur: '11:30', done: true },
    { n: 'Intrinsic hand muscles II', dur: '13:14', done: true },
    { n: 'Clinical correlations', dur: '22:48', done: false, current: true },
    { n: 'Quick recap & viva tips', dur: '9:55', done: false },
    { n: 'Practice MCQs', dur: '15:00', done: false },
  ];
  return (
    <Phone>
      <div style={{ flex:1, overflow:'auto', background: T.bg }}>
        {/* Header */}
        <div style={{
          background: T.brand, color: '#fff',
          padding: '54px 20px 22px', borderRadius: '0 0 28px 28px',
          position:'relative', overflow:'hidden',
        }}>
          <div style={{ position:'absolute', top: -30, right: -30, width: 160, height: 160, borderRadius:'50%', background:'rgba(255,255,255,0.06)' }}/>
          <div style={{ position:'relative' }}>
            <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom: 18 }}>
              <button onClick={onBack} style={{ width:38, height:38, borderRadius: 10, background:'rgba(255,255,255,0.14)', border:0, color:'#fff', display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
                <I.back/>
              </button>
              <button style={iconBtn('#fff')}><I.search/></button>
            </div>
            <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1.2, opacity: 0.7, textTransform:'uppercase', marginBottom: 6 }}>
              Dashboard · Anatomy · Module 02
            </div>
            <div style={{ fontFamily: T.serif, fontSize: 26, letterSpacing: -0.3, lineHeight: 1.15 }}>Muscle Compartments</div>
            <div style={{ display:'flex', gap: 22, marginTop: 18, paddingTop: 14, borderTop: '1px solid rgba(255,255,255,0.15)' }}>
              <div>
                <div style={{ fontFamily: T.serif, fontSize: 18, letterSpacing: -0.3 }}>9</div>
                <div style={{ fontFamily: T.mono, fontSize: 10, opacity: 0.7, letterSpacing: 0.5, textTransform:'uppercase' }}>Videos</div>
              </div>
              <div>
                <div style={{ fontFamily: T.serif, fontSize: 18, letterSpacing: -0.3 }}>2h 14m</div>
                <div style={{ fontFamily: T.mono, fontSize: 10, opacity: 0.7, letterSpacing: 0.5, textTransform:'uppercase' }}>Duration</div>
              </div>
              <div>
                <div style={{ fontFamily: T.serif, fontSize: 18, letterSpacing: -0.3 }}>6/9</div>
                <div style={{ fontFamily: T.mono, fontSize: 10, opacity: 0.7, letterSpacing: 0.5, textTransform:'uppercase' }}>Complete</div>
              </div>
            </div>
          </div>
        </div>

        <div style={{ padding: '20px 20px 0' }}>
          <SearchBar placeholder="Find a video…"/>
        </div>
        <div style={{ display:'flex', gap: 8, padding: '14px 20px 6px' }}>
          <Chip active>Videos (9)</Chip>
          <Chip>Resources (3)</Chip>
        </div>

        <div style={{ padding: '8px 20px 0' }}>
          {vids.map((v, i) => (
            <div key={i} onClick={v.current ? onOpenVideo : undefined} style={{
              display:'flex', alignItems:'center', gap: 12, padding: '12px 12px',
              background: v.current ? T.brandTint : T.surface,
              border: `1px solid ${v.current ? T.brand : T.hair2}`,
              borderRadius: T.r.m, marginBottom: 8, cursor: v.current ? 'pointer' : 'default',
            }}>
              <div style={{ position:'relative', width: 72, height: 56, borderRadius: 8, overflow:'hidden', flexShrink:0 }}>
                <Placeholder w="100%" h="100%" tone={v.done ? 'cool' : 'teal'} label="" radius={0}/>
                <div style={{ position:'absolute', inset:0, display:'flex', alignItems:'center', justifyContent:'center' }}>
                  {v.done ? (
                    <I.checkC style={{ color: T.ok, background:'#fff', borderRadius:'50%' }}/>
                  ) : (
                    <div style={{ width: 26, height: 26, borderRadius:'50%', background:'rgba(255,255,255,0.95)', color: T.brand, display:'flex', alignItems:'center', justifyContent:'center' }}>
                      <I.play width={14} height={14}/>
                    </div>
                  )}
                </div>
              </div>
              <div style={{ flex:1, minWidth:0 }}>
                <div style={{ fontFamily: T.mono, fontSize: 9, color: T.ink50, letterSpacing: 0.5, textTransform:'uppercase' }}>Video {String(i+1).padStart(2,'0')}</div>
                <div style={{ fontFamily: T.sans, fontSize: 13, fontWeight: v.current ? 600 : 500, color: T.ink, marginTop: 2, lineHeight: 1.3, overflow:'hidden', textOverflow:'ellipsis', whiteSpace:'nowrap' }}>{v.n}</div>
                <div style={{ display:'flex', alignItems:'center', gap: 4, marginTop: 4, fontFamily: T.sans, fontSize: 11, color: T.ink50 }}>
                  <I.clock width={11} height={11}/>{v.dur}
                  {v.current && <span style={{ marginLeft: 6, color: T.brand, fontWeight: 600, fontFamily: T.mono, fontSize: 9, letterSpacing: 0.5, textTransform:'uppercase' }}>Up next</span>}
                </div>
              </div>
              <I.chev style={{ color: T.ink30, flexShrink:0 }}/>
            </div>
          ))}
          <div style={{ textAlign:'center', padding: '14px 0 32px', fontFamily: T.mono, fontSize: 10, color: T.ink50, letterSpacing: 0.5, textTransform:'uppercase' }}>
            All 9 videos loaded
          </div>
        </div>
      </div>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Video Player
// ──────────────────────────────────────────────────────────────
function VideoScreen({ onBack }) {
  return (
    <Phone dark statusDark>
      <div style={{ flex:1, overflow:'auto', background:'#0A1412', color:'#fff', position:'relative' }}>
        {/* Player */}
        <div style={{ position:'relative', paddingTop: 44 }}>
          <Placeholder h={240} tone="ink" label="video frame" radius={0}/>
          {/* Top controls */}
          <div style={{ position:'absolute', top: 54, left: 0, right: 0, padding: '0 16px', display:'flex', justifyContent:'space-between', zIndex: 5 }}>
            <button onClick={onBack} style={{ width: 38, height: 38, borderRadius: 10, background:'rgba(255,255,255,0.18)', backdropFilter:'blur(12px)', border: 0, color: '#fff', display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}><I.back/></button>
            <div style={{ display:'flex', gap: 6 }}>
              <button style={{ width: 38, height: 38, borderRadius: 10, background:'rgba(255,255,255,0.18)', backdropFilter:'blur(12px)', border: 0, color: '#fff', display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}><I.download/></button>
              <button style={{ width: 38, height: 38, borderRadius: 10, background:'rgba(255,255,255,0.18)', backdropFilter:'blur(12px)', border: 0, color: '#fff', display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
                <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round"><path d="M3 9V5a2 2 0 012-2h4M21 9V5a2 2 0 00-2-2h-4M3 15v4a2 2 0 002 2h4M21 15v4a2 2 0 01-2 2h-4"/></svg>
              </button>
            </div>
          </div>
          {/* Center play */}
          <div style={{ position:'absolute', inset: '44px 0 0 0', display:'flex', alignItems:'center', justifyContent:'center', pointerEvents:'none' }}>
            <div style={{ width: 64, height: 64, borderRadius:'50%', background: 'rgba(255,255,255,0.96)', color: T.brand, display:'flex', alignItems:'center', justifyContent:'center' }}>
              <I.play width={26} height={26}/>
            </div>
          </div>
          {/* Bottom progress */}
          <div style={{ position:'absolute', bottom: 14, left: 16, right: 16 }}>
            <div style={{ height: 4, background:'rgba(255,255,255,0.25)', borderRadius: 99, position:'relative' }}>
              <div style={{ width: '42%', height: '100%', background:'#fff', borderRadius: 99 }}/>
              <div style={{ position:'absolute', left: '42%', top: '50%', width: 12, height: 12, borderRadius:'50%', background:'#fff', transform: 'translate(-50%, -50%)' }}/>
            </div>
            <div style={{ display:'flex', justifyContent:'space-between', marginTop: 6, fontFamily: T.mono, fontSize: 10, color:'rgba(255,255,255,0.8)' }}>
              <span>09:34</span><span>22:48</span>
            </div>
          </div>
        </div>

        {/* Below player */}
        <div style={{ padding: '20px 20px 0' }}>
          <Chip tone="soft" style={{ background:'rgba(14,110,90,0.22)', color:'#9DDDC8', border:0 }}>Module 02 · Video 07</Chip>
          <div style={{ fontFamily: T.serif, fontSize: 22, color: '#fff', marginTop: 12, letterSpacing: -0.3, lineHeight: 1.2 }}>Clinical correlations: nerve injuries</div>
          <div style={{ display:'flex', alignItems:'center', gap: 18, marginTop: 14, fontFamily: T.sans, fontSize: 12, color:'rgba(255,255,255,0.65)' }}>
            <div style={{ display:'flex', alignItems:'center', gap: 6 }}><I.clock width={12} height={12}/>22:48</div>
            <div style={{ display:'flex', alignItems:'center', gap: 6 }}><I.eye width={14} height={14}/>2,481 watching</div>
          </div>

          {/* Quick controls */}
          <div style={{ display:'flex', gap: 8, marginTop: 18 }}>
            {[
              { l: '720p', i: <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" strokeWidth="1.7"><rect x="3" y="6" width="18" height="12" rx="2"/></svg> },
              { l: '1.0x', i: <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor"><path d="M8 5v14l11-7z"/></svg> },
              { l: 'CC', i: <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" strokeWidth="1.7"><rect x="3" y="5" width="18" height="14" rx="2"/></svg> },
              { l: 'Autoplay', i: null },
            ].map((c, i) => (
              <Chip key={i} leading={c.i} style={{ background: 'rgba(255,255,255,0.08)', color:'#fff', border: '1px solid rgba(255,255,255,0.08)' }}>{c.l}</Chip>
            ))}
          </div>

          {/* Mentor */}
          <Card style={{ background:'rgba(255,255,255,0.06)', border:'1px solid rgba(255,255,255,0.08)', display:'flex', alignItems:'center', gap: 12, marginTop: 20 }}>
            <Avatar name="Dr. Tanvir Hossain" size={40}/>
            <div style={{ flex:1 }}>
              <div style={{ display:'flex', alignItems:'center', gap: 6 }}>
                <div style={{ fontFamily: T.sans, fontSize: 13, fontWeight: 600, color:'#fff' }}>Dr. Tanvir Hossain</div>
                <Verified/>
              </div>
              <div style={{ fontFamily: T.sans, fontSize: 11, color:'rgba(255,255,255,0.55)', marginTop: 2 }}>Anatomy · DMC faculty</div>
            </div>
            <Btn size="sm" variant="secondary" style={{ background:'rgba(255,255,255,0.12)', color:'#fff', border:'none' }}>Follow</Btn>
          </Card>

          {/* Up next */}
          <div style={{ marginTop: 24 }}>
            <div style={{ fontFamily: T.mono, fontSize: 10, color:'rgba(255,255,255,0.5)', letterSpacing: 1, textTransform:'uppercase', marginBottom: 12 }}>Up next</div>
            {[
              { n: 'Quick recap & viva tips', dur: '9:55' },
              { n: 'Practice MCQs', dur: '15:00' },
            ].map((v, i) => (
              <div key={i} style={{ display:'flex', alignItems:'center', gap: 12, padding: '10px 0', borderTop: i===0 ? 'none' : '1px solid rgba(255,255,255,0.08)' }}>
                <Placeholder w={64} h={48} tone="ink" label="" radius={8}/>
                <div style={{ flex:1, minWidth:0 }}>
                  <div style={{ fontFamily: T.sans, fontSize: 13, color:'#fff', fontWeight: 500, lineHeight: 1.3 }}>{v.n}</div>
                  <div style={{ fontFamily: T.sans, fontSize: 11, color:'rgba(255,255,255,0.55)', marginTop: 3 }}>{v.dur}</div>
                </div>
                <I.play style={{ color:'rgba(255,255,255,0.6)' }}/>
              </div>
            ))}
          </div>

          <div style={{ height: 24 }}/>
        </div>
      </div>
    </Phone>
  );
}

Object.assign(window, { StudyScreen, StudyRoomScreen, ModuleScreen, VideoScreen });
