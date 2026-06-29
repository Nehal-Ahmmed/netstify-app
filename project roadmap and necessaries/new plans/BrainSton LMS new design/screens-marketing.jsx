// screens-marketing.jsx — Splash, Home, Courses, Course Details, Course List, Articles, Article Detail, Mentor

// ──────────────────────────────────────────────────────────────
// Splash
// ──────────────────────────────────────────────────────────────
function SplashScreen() {
  return (
    <Phone>
      <div style={{ flex:1, background: T.brand, display:'flex', flexDirection:'column', alignItems:'center', justifyContent:'center', position:'relative' }}>
        {/* Background pattern */}
        <div style={{ position:'absolute', inset:0, opacity: 0.08, background: 'radial-gradient(circle at 30% 20%, #fff 0, transparent 40%), radial-gradient(circle at 70% 80%, #fff 0, transparent 40%)' }}/>
        <div style={{ width: 96, height: 96, borderRadius: 28, background: '#fff', display:'flex', alignItems:'center', justifyContent:'center', marginBottom: 28, boxShadow: '0 24px 60px -12px rgba(0,0,0,0.3)' }}>
          <BrandMark size={56} color={T.brand}/>
        </div>
        <div style={{ fontFamily: T.serif, fontSize: 42, color:'#fff', letterSpacing:-0.5, lineHeight: 1 }}>MediShark</div>
        <div style={{ fontFamily: T.sans, fontSize: 13, color: 'rgba(255,255,255,0.7)', marginTop: 10, letterSpacing: 1, textTransform:'uppercase' }}>Learn medicine, smarter</div>
        <div style={{ position:'absolute', bottom: 80, display:'flex', flexDirection:'column', alignItems:'center', gap: 14 }}>
          <div style={{
            width: 36, height: 36, borderRadius: '50%',
            border: '2.5px solid rgba(255,255,255,0.2)', borderTopColor: '#fff',
            animation: 'spin 0.9s linear infinite',
          }}/>
          <div style={{ fontFamily: T.mono, fontSize: 10, color: 'rgba(255,255,255,0.5)', letterSpacing: 1 }}>POWERED BY BRAINSTON LMS</div>
        </div>
        <style>{`@keyframes spin{to{transform:rotate(360deg)}}`}</style>
      </div>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Home tab (landing)
// ──────────────────────────────────────────────────────────────
function HomeScreen({ onOpenDrawer, onOpenCourse, onOpenCart }) {
  const cats = [
    { tag: 'MBBS · YEAR 1-2', title: 'Anatomy Sprint', sub: '12 modules · 84 videos', tone: 'teal' },
    { tag: 'USMLE · STEP 1', title: 'Biochem Crash', sub: '6 modules · 42 videos', tone: 'sand' },
    { tag: 'FCPS · PART 1', title: 'Pharmacology Lab', sub: '9 modules · 61 videos', tone: 'coral' },
  ];
  const [slide, setSlide] = React.useState(0);
  React.useEffect(() => {
    const t = setInterval(() => setSlide(s => (s+1) % cats.length), 3500);
    return () => clearInterval(t);
  }, []);

  return (
    <Phone>
      <AppBar variant="default" onMenu={onOpenDrawer} onSearch={()=>{}} onCart={onOpenCart} cartCount={2} onAvatar={()=>{}}/>
      <div style={{ flex:1, overflow:'auto', background: T.bg }}>
        {/* Hero */}
        <div style={{ padding: '28px 20px 24px', background: T.surface, borderBottom: `1px solid ${T.hair2}` }}>
          <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1.5, color: T.brand, textTransform:'uppercase', marginBottom: 14 }}>
            • Spring intake · Now open
          </div>
          <div style={{ fontFamily: T.serif, fontSize: 38, color: T.ink, lineHeight: 1.05, letterSpacing: -0.5, marginBottom: 12 }}>
            Med school,<br/>
            <span style={{ fontStyle:'italic', color: T.brand }}>without the burnout.</span>
          </div>
          <div style={{ fontFamily: T.sans, fontSize: 14, color: T.ink50, lineHeight: 1.5, marginBottom: 22, maxWidth: 320 }}>
            Concise video lessons, practice exams, and mentor-led tracks built for MBBS, USMLE, and FCPS.
          </div>
          <div style={{ display:'flex', gap: 10 }}>
            <Btn variant="primary" size="md" trailing={<I.arrR/>}>Browse Courses</Btn>
            <Btn variant="secondary" size="md">Articles</Btn>
          </div>
        </div>

        {/* Stats */}
        <div style={{ background: T.surface, padding: '0 20px 20px', display:'flex', alignItems:'center', justifyContent:'space-between', borderBottom: `1px solid ${T.hair2}` }}>
          {[
            { v: '10k+', l: 'Students' },
            { v: '50+',  l: 'Courses' },
            { v: '12+',  l: 'Mentors' },
          ].map((s, i) => (
            <React.Fragment key={i}>
              <div style={{ flex:1, textAlign:'center' }}>
                <div style={{ fontFamily: T.serif, fontSize: 26, color: T.ink, letterSpacing: -0.3 }}>{s.v}</div>
                <div style={{ fontFamily: T.mono, fontSize: 10, color: T.ink50, marginTop: 2, letterSpacing: 0.8, textTransform:'uppercase' }}>{s.l}</div>
              </div>
              {i < 2 && <div style={{ width: 1, height: 28, background: T.hair }}/>}
            </React.Fragment>
          ))}
        </div>

        {/* Featured carousel */}
        <div style={{ padding: '24px 20px 0' }}>
          <SectionHead kicker="Featured" title="Tracks for you" action="See all"/>
        </div>
        <div style={{ padding: '0 20px', position:'relative' }}>
          <Card p={0} style={{ overflow:'hidden', cursor:'pointer' }} onClick={onOpenCourse}>
            <Placeholder h={172} tone={cats[slide].tone} label={cats[slide].title.toLowerCase()} radius={0}/>
            <div style={{ padding: 16 }}>
              <Chip tone="soft" style={{ marginBottom: 8 }}>{cats[slide].tag}</Chip>
              <div style={{ fontFamily: T.sans, fontSize: 17, fontWeight: 600, color: T.ink, letterSpacing: -0.2 }}>{cats[slide].title}</div>
              <div style={{ fontFamily: T.sans, fontSize: 13, color: T.ink50, marginTop: 4 }}>{cats[slide].sub}</div>
            </div>
          </Card>
          <div style={{ display:'flex', justifyContent:'center', gap: 6, marginTop: 14 }}>
            {cats.map((_, i) => (
              <div key={i} onClick={()=>setSlide(i)} style={{
                width: slide===i ? 22 : 6, height: 6, borderRadius: 4,
                background: slide===i ? T.brand : T.ink10, cursor:'pointer', transition: 'all .3s',
              }}/>
            ))}
          </div>
        </div>

        {/* Mentors */}
        <div style={{ padding: '28px 0 0' }}>
          <div style={{ padding: '0 20px' }}>
            <SectionHead kicker="Faculty" title="Top mentors" action="View all"/>
          </div>
          <div style={{ display:'flex', gap: 12, overflowX:'auto', padding: '0 20px 4px' }}>
            {[
              { n: 'Dr. Tanvir Hossain', q: 'Anatomy · DMC' },
              { n: 'Dr. Nasrin Akter', q: 'Biochem · CMC' },
              { n: 'Dr. Rakib Khan', q: 'Pharma · BSMMU' },
              { n: 'Dr. Sumi Begum', q: 'Physiology' },
            ].map((m, i) => (
              <div key={i} style={{
                width: 132, flexShrink:0, background: T.surface, borderRadius: T.r.l,
                padding: 14, border: `1px solid ${T.hair2}`, textAlign:'center',
              }}>
                <div style={{ position:'relative', display:'inline-block' }}>
                  <Avatar name={m.n} size={56}/>
                  <div style={{ position:'absolute', bottom: -2, right: -2, background:'#fff', borderRadius:'50%', padding:1 }}>
                    <Verified size={16}/>
                  </div>
                </div>
                <div style={{ fontFamily: T.sans, fontSize: 13, fontWeight: 600, color: T.ink, marginTop: 10, letterSpacing: -0.1 }}>{m.n}</div>
                <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50, marginTop: 2 }}>{m.q}</div>
              </div>
            ))}
          </div>
        </div>

        {/* Why MediShark */}
        <div style={{ padding: '32px 20px 8px' }}>
          <div style={{ textAlign:'center', marginBottom: 20 }}>
            <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1.5, color: T.brand, textTransform:'uppercase', marginBottom: 8 }}>Why MediShark</div>
            <div style={{ fontFamily: T.serif, fontSize: 26, color: T.ink, letterSpacing: -0.3, lineHeight: 1.1 }}>Built by doctors,<br/>tested in the wards.</div>
          </div>
          {[
            { i: I.play, t: 'Short, surgical lessons', d: '8-12 min videos written for exams, not lectures.' },
            { i: I.trophy, t: 'Mock exams, real grades', d: 'Track progress against your year cohort.' },
            { i: I.book, t: 'Notes you can keep', d: 'Download every PDF, watch offline, sync devices.' },
          ].map((f, i) => (
            <div key={i} style={{
              display:'flex', gap: 14, padding: 16,
              background: T.surface, borderRadius: T.r.l, border:`1px solid ${T.hair2}`,
              marginBottom: 10,
            }}>
              <div style={{ width: 44, height: 44, borderRadius: 12, background: T.brandSoft, color: T.brand, display:'flex', alignItems:'center', justifyContent:'center', flexShrink:0 }}>
                <f.i/>
              </div>
              <div>
                <div style={{ fontFamily: T.sans, fontSize: 14, fontWeight: 600, color: T.ink }}>{f.t}</div>
                <div style={{ fontFamily: T.sans, fontSize: 12, color: T.ink50, marginTop: 4, lineHeight: 1.5 }}>{f.d}</div>
              </div>
            </div>
          ))}
        </div>

        {/* Reviews */}
        <div style={{ padding: '24px 0 0' }}>
          <div style={{ padding: '0 20px' }}>
            <SectionHead kicker="Reviews" title="Loved by students" action="More"/>
          </div>
          <div style={{ display:'flex', gap: 12, overflowX:'auto', padding: '0 20px 4px' }}>
            {[
              { n: 'Mehedi Hasan', q: 'The Anatomy track got me through 2nd prof. Lectures actually stick.' },
              { n: 'Sumaiya Khan',  q: 'Best in Bangla med-ed. Period. The mentors reply to comments too.' },
              { n: 'Imran Hossain', q: 'Spent half what coaching cost. Scored higher than my batchmates.' },
            ].map((r, i) => (
              <div key={i} style={{
                width: 240, flexShrink:0, background: T.surface, borderRadius: T.r.l,
                padding: 16, border: `1px solid ${T.hair2}`,
              }}>
                <Stars value={5}/>
                <div style={{ fontFamily: T.serif, fontSize: 16, color: T.ink, lineHeight: 1.4, marginTop: 10, fontStyle:'italic' }}>“{r.q}”</div>
                <div style={{ display:'flex', alignItems:'center', gap: 10, marginTop: 14 }}>
                  <Avatar name={r.n} size={32}/>
                  <div style={{ fontFamily: T.sans, fontSize: 13, fontWeight: 500, color: T.ink }}>{r.n}</div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* FAQ */}
        <div style={{ padding: '28px 20px' }}>
          <SectionHead kicker="FAQ" title="Common questions"/>
          {[
            'How long do I keep access to a course?',
            'Can I download lessons for offline study?',
            'Are exams included?',
          ].map((q, i) => (
            <div key={i} style={{
              display:'flex', alignItems:'center', padding: '16px 0',
              borderTop: i===0 ? 'none' : `1px solid ${T.hair2}`,
            }}>
              <div style={{ flex:1, fontFamily: T.sans, fontSize: 14, color: T.ink, fontWeight: 500 }}>{q}</div>
              <div style={{ width: 32, height: 32, borderRadius: '50%', background: T.brandSoft, color: T.brand, display:'flex', alignItems:'center', justifyContent:'center' }}>
                <I.plus/>
              </div>
            </div>
          ))}
        </div>

        <NavSpacer/>
      </div>
      <BottomNav active="home"/>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Courses tab (category catalog)
// ──────────────────────────────────────────────────────────────
function CoursesScreen({ onOpenDrawer, onOpenList, onOpenCart }) {
  const cats = [
    { badge: 'UG', title: 'Undergraduate', desc: 'MBBS, BDS, DPT — built around your year, profs, and finals.', count: 24, tone: 'teal' },
    { badge: 'PG', title: 'Postgraduate', desc: 'FCPS Part 1 & 2, MD/MS preparation, residency interviews.', count: 18, tone: 'sand' },
    { badge: 'GP', title: 'General Practice', desc: 'CME credits, clinical skills, and bedside refreshers.', count: 12, tone: 'coral' },
  ];
  return (
    <Phone>
      <AppBar variant="default" onMenu={onOpenDrawer} onSearch={()=>{}} onCart={onOpenCart} cartCount={2} onAvatar={()=>{}}/>
      <div style={{ flex:1, overflow:'auto', background: T.bg }}>
        <div style={{ padding: '24px 20px 16px', textAlign:'center' }}>
          <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1.5, color: T.brand, textTransform:'uppercase', marginBottom: 8 }}>Catalog</div>
          <div style={{ fontFamily: T.serif, fontSize: 30, color: T.ink, letterSpacing: -0.3, lineHeight: 1.1 }}>Find your track</div>
          <div style={{ fontFamily: T.sans, fontSize: 13, color: T.ink50, marginTop: 8, lineHeight: 1.5, maxWidth: 280, margin: '8px auto 0' }}>54 active courses across undergraduate, postgraduate, and clinical practice.</div>
        </div>

        <div style={{ padding: '8px 20px 0' }}>
          {cats.map((c, i) => (
            <div key={i} onClick={onOpenList} style={{
              background: T.surface, borderRadius: T.r.xl, marginBottom: 14,
              border: `1px solid ${T.hair2}`, overflow: 'hidden', cursor:'pointer',
              display:'grid', gridTemplateColumns: '100px 1fr',
            }}>
              <Placeholder h="100%" w="100%" tone={c.tone} label={c.badge} radius={0}/>
              <div style={{ padding: '16px 16px 14px' }}>
                <div style={{ display:'flex', alignItems:'center', gap: 8, marginBottom: 6 }}>
                  <Chip tone="soft" style={{ padding: '3px 8px', fontSize: 10, fontWeight: 700, letterSpacing: 0.5 }}>{c.badge}</Chip>
                  <div style={{ fontFamily: T.mono, fontSize: 10, color: T.ink50 }}>{c.count} COURSES</div>
                </div>
                <div style={{ fontFamily: T.sans, fontSize: 17, fontWeight: 600, color: T.ink, letterSpacing: -0.2, marginBottom: 4 }}>{c.title}</div>
                <div style={{ fontFamily: T.sans, fontSize: 12, color: T.ink50, lineHeight: 1.45, marginBottom: 10 }}>{c.desc}</div>
                <div style={{ display:'flex', alignItems:'center', gap: 4, fontFamily: T.sans, fontSize: 13, color: T.brand, fontWeight: 600 }}>
                  View programs <I.arrR width={14} height={14}/>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* Featured bundle */}
        <div style={{ padding: '8px 20px 0' }}>
          <div style={{
            background: T.surfaceDk, color: '#fff', borderRadius: T.r.xl,
            padding: 20, position: 'relative', overflow: 'hidden',
          }}>
            <div style={{ position:'absolute', top: -20, right: -30, width: 180, height: 180, borderRadius:'50%', background: 'rgba(14,110,90,0.25)' }}/>
            <div style={{ position:'relative' }}>
              <Chip tone="brand" style={{ marginBottom: 12 }}>Limited bundle</Chip>
              <div style={{ fontFamily: T.serif, fontSize: 22, lineHeight: 1.15, marginBottom: 6 }}>Year 1+2 Master Pack</div>
              <div style={{ fontFamily: T.sans, fontSize: 12, opacity: 0.7, marginBottom: 16 }}>Anatomy, Physiology, Biochem, Histology — together.</div>
              <Btn variant="secondary" size="md" style={{ background:'#fff' }}>See bundle</Btn>
            </div>
          </div>
        </div>

        <NavSpacer/>
      </div>
      <BottomNav active="courses"/>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Course List (filtered)
// ──────────────────────────────────────────────────────────────
function CourseListScreen({ onBack, onOpenCourse }) {
  const items = [
    { t: 'Anatomy of the Upper Limb', s: 'Year 1-2 · 28 lessons', m: 'Dr. Tanvir Hossain', dur: '6h 20m', price: 2400, was: 3200, tone: 'teal' },
    { t: 'Biochemistry: Carbohydrate Metabolism', s: 'Year 2 · 22 lessons', m: 'Dr. Nasrin Akter', dur: '4h 45m', price: 1900, tone: 'sand' },
    { t: 'Physiology: Cardiovascular System', s: 'Year 1 · 34 lessons', m: 'Dr. Sumi Begum', dur: '8h 10m', price: 2800, was: 3600, tone: 'coral' },
    { t: 'Histology: Slide-by-Slide', s: 'Year 1-2 · 18 lessons', m: 'Dr. Rakib Khan', dur: '3h 30m', price: 1500, tone: 'cool' },
  ];
  return (
    <Phone>
      <AppBar variant="title" title="Undergraduate" onBack={onBack} trailing={
        <button style={iconBtn(T.ink)}><I.search/></button>
      }/>
      <div style={{ flex:1, overflow:'auto', background: T.bg }}>
        {/* Filter row 1 */}
        <div style={{ display:'flex', gap: 8, padding: '14px 20px 8px', overflowX:'auto' }}>
          <Chip active>All Types</Chip>
          <Chip>Test Series</Chip>
          <Chip leading={<I.filter/>}>Latest</Chip>
        </div>
        {/* Category */}
        <div style={{ display:'flex', gap: 8, padding: '4px 20px', overflowX:'auto' }}>
          {['Anatomy', 'Biochem', 'Physiology', 'Pharma', 'Pathology'].map((c, i) => (
            <Chip key={c} active={i===0} tone={i===0 ? 'soft' : 'ghost'}>{c}</Chip>
          ))}
        </div>
        {/* Year tags */}
        <div style={{ display:'flex', gap: 8, padding: '8px 20px 16px' }}>
          {['1-2', '3-4', '5'].map((y, i) => (
            <Chip key={y} active={i===0}>Year {y}</Chip>
          ))}
        </div>

        {/* List */}
        <div style={{ padding: '0 20px' }}>
          {items.map((it, i) => (
            <Card key={i} p={0} style={{ marginBottom: 14, overflow:'hidden' }} onClick={i===0 ? onOpenCourse : undefined}>
              <div style={{ position:'relative' }}>
                <Placeholder h={140} tone={it.tone} label={it.t} radius={0}/>
                <div style={{ position:'absolute', top: 10, right: 10, display:'flex', gap: 8 }}>
                  <button style={{ width:34, height:34, borderRadius:'50%', background:'rgba(255,255,255,0.94)', border:0, color: i===0 ? T.coral : T.ink70, display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
                    {i===0 ? <I.heartF/> : <I.heart/>}
                  </button>
                  <button style={{ width:34, height:34, borderRadius:'50%', background:'rgba(255,255,255,0.94)', border:0, color: T.ink70, display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
                    <I.cart/>
                  </button>
                </div>
              </div>
              <div style={{ padding: 14 }}>
                <div style={{ fontFamily: T.sans, fontSize: 15, fontWeight: 600, color: T.ink, lineHeight: 1.3, letterSpacing: -0.2 }}>{it.t}</div>
                <div style={{ fontFamily: T.sans, fontSize: 12, color: T.ink50, marginTop: 4 }}>{it.s}</div>
                <div style={{ display:'flex', alignItems:'center', gap: 10, marginTop: 10, paddingTop: 10, borderTop: `1px solid ${T.hair2}` }}>
                  <Avatar name={it.m} size={22}/>
                  <div style={{ fontFamily: T.sans, fontSize: 12, color: T.ink70 }}>{it.m}</div>
                  <div style={{ flex:1 }}/>
                  <div style={{ display:'flex', alignItems:'center', gap: 4, fontFamily: T.sans, fontSize: 12, color: T.ink50 }}>
                    <I.clock/>{it.dur}
                  </div>
                </div>
                <div style={{ display:'flex', alignItems:'baseline', gap: 8, marginTop: 10 }}>
                  <div style={{ fontFamily: T.serif, fontSize: 22, color: T.ink, letterSpacing: -0.3 }}>৳{it.price}</div>
                  {it.was && <div style={{ fontFamily: T.sans, fontSize: 13, color: T.ink50, textDecoration:'line-through' }}>৳{it.was}</div>}
                  {it.was && <Chip tone="coral" style={{ padding: '2px 8px', fontSize: 10, fontWeight: 700 }}>{Math.round((1-it.price/it.was)*100)}% OFF</Chip>}
                </div>
              </div>
            </Card>
          ))}
        </div>
        <NavSpacer h={32}/>
      </div>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Course Details
// ──────────────────────────────────────────────────────────────
function CourseDetailScreen({ onBack, onAddToCart, onOpenSheet }) {
  return (
    <Phone>
      <div style={{ flex:1, overflow:'auto', background: T.bg, position:'relative' }}>
        {/* Hero image */}
        <div style={{ position:'relative' }}>
          <Placeholder h={300} tone="teal" label="anatomy: upper limb" radius={0}/>
          {/* Gradient overlay */}
          <div style={{ position:'absolute', inset:0, background: 'linear-gradient(180deg, rgba(15,26,24,0.3) 0%, transparent 30%, rgba(15,26,24,0.65) 100%)' }}/>
          {/* Top bar */}
          <div style={{ position:'absolute', top: 54, left: 0, right: 0, padding: '0 16px', display:'flex', justifyContent:'space-between', zIndex:5 }}>
            <button onClick={onBack} style={{ width:40, height:40, borderRadius: 12, background:'rgba(255,255,255,0.92)', border:0, color:T.ink, display:'flex', alignItems:'center', justifyContent:'center', backdropFilter:'blur(12px)', cursor:'pointer' }}>
              <I.back/>
            </button>
            <div style={{ display:'flex', gap: 8 }}>
              <button style={{ width:40, height:40, borderRadius: 12, background:'rgba(255,255,255,0.92)', border:0, color:T.ink, display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
                <I.search/>
              </button>
              <button style={{ width:40, height:40, borderRadius: 12, background:'rgba(255,255,255,0.92)', border:0, color:T.ink, display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
                <I.cart/>
              </button>
            </div>
          </div>
          {/* Hero text */}
          <div style={{ position:'absolute', bottom: 38, left: 20, right: 20, color: '#fff' }}>
            <Chip tone="brand" style={{ background:'rgba(255,255,255,0.18)', color:'#fff', backdropFilter:'blur(12px)', border:0 }}>YEAR 1-2 · MBBS</Chip>
            <div style={{ fontFamily: T.serif, fontSize: 30, marginTop: 14, letterSpacing: -0.3, lineHeight: 1.1 }}>Anatomy of the<br/>Upper Limb</div>
            <div style={{ fontFamily: T.sans, fontSize: 13, opacity: 0.85, marginTop: 8 }}>Bones, muscles, vasculature — slide by slide.</div>
          </div>
          {/* Stat card overlap */}
          <div style={{ position:'absolute', bottom: -28, left: 20, background: T.surface, borderRadius: T.r.m, padding: '12px 16px', border:`1px solid ${T.hair2}`, boxShadow: T.shadowCard, display:'flex', gap: 18 }}>
            <div>
              <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50 }}>Classes</div>
              <div style={{ fontFamily: T.serif, fontSize: 20, color: T.ink, letterSpacing: -0.3 }}>28</div>
            </div>
            <div style={{ width:1, background: T.hair }}/>
            <div>
              <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50 }}>Duration</div>
              <div style={{ fontFamily: T.serif, fontSize: 20, color: T.ink, letterSpacing: -0.3 }}>6h 20m</div>
            </div>
            <div style={{ width:1, background: T.hair }}/>
            <div>
              <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50 }}>Rating</div>
              <div style={{ display:'flex', alignItems:'center', gap: 4 }}>
                <div style={{ fontFamily: T.serif, fontSize: 20, color: T.ink, letterSpacing: -0.3 }}>4.9</div>
                <I.star style={{ color: T.warn }}/>
              </div>
            </div>
          </div>
        </div>

        {/* Pricing card */}
        <div style={{ padding: '48px 20px 8px' }}>
          <Card>
            <div style={{ display:'flex', alignItems:'flex-start', justifyContent:'space-between', marginBottom: 14 }}>
              <div>
                <div style={{ display:'flex', alignItems:'baseline', gap: 10 }}>
                  <div style={{ fontFamily: T.serif, fontSize: 32, color: T.ink, letterSpacing: -0.5 }}>৳2,400</div>
                  <div style={{ fontFamily: T.sans, fontSize: 14, color: T.ink50, textDecoration:'line-through' }}>৳3,200</div>
                </div>
                <Chip tone="coral" style={{ marginTop: 6, padding:'3px 10px', fontSize: 11, fontWeight: 700 }}>25% off · Spring intake</Chip>
              </div>
              <button style={{ width:40, height:40, borderRadius:'50%', background: T.coralSoft, color: T.coral, border:0, display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
                <I.heartF/>
              </button>
            </div>
            <div style={{ display:'flex', gap: 8 }}>
              <Btn variant="primary" size="lg" full>Enroll now</Btn>
              <Btn variant="secondary" size="lg" onClick={onAddToCart} style={{ width: 52, padding: 0 }}><I.cart/></Btn>
            </div>
            <div style={{ display:'flex', alignItems:'center', gap: 8, marginTop: 14, paddingTop: 14, borderTop: `1px solid ${T.hair2}` }}>
              <I.checkC style={{ color: T.ok }}/>
              <div style={{ fontFamily: T.sans, fontSize: 12, color: T.ink70 }}>Lifetime access · Download & study offline</div>
            </div>
          </Card>
        </div>

        {/* Mentors */}
        <div style={{ padding: '20px 20px 0' }}>
          <SectionHead kicker="Faculty" title="Course mentors"/>
          <Card style={{ display:'flex', alignItems:'center', gap: 14, padding: 14 }}>
            <Avatar name="Dr. Tanvir Hossain" size={48}/>
            <div style={{ flex:1 }}>
              <div style={{ display:'flex', alignItems:'center', gap: 6 }}>
                <div style={{ fontFamily: T.sans, fontSize: 14, fontWeight: 600, color: T.ink }}>Dr. Tanvir Hossain</div>
                <Verified/>
              </div>
              <div style={{ fontFamily: T.sans, fontSize: 12, color: T.ink50, marginTop: 2 }}>MBBS · MS Anatomy · DMC Faculty</div>
            </div>
            <I.chev style={{ color: T.ink30 }}/>
          </Card>
        </div>

        {/* Bundle modules with preview */}
        <div style={{ padding: '20px 20px 0' }}>
          <SectionHead kicker="Modules" title="What's inside"/>
          {[
            { n: 'Bones of the Upper Limb', v: 7 },
            { n: 'Muscle Compartments', v: 9 },
            { n: 'Vasculature & Nerves', v: 8 },
          ].map((m, i) => (
            <Card key={i} style={{ marginBottom: 10, display:'flex', alignItems:'center', gap: 12 }}>
              <div style={{ width: 40, height: 40, borderRadius: 10, background: T.brandSoft, color: T.brandDeep, display:'flex', alignItems:'center', justifyContent:'center', fontFamily: T.serif, fontSize: 18 }}>
                {String(i+1).padStart(2,'0')}
              </div>
              <div style={{ flex:1 }}>
                <div style={{ fontFamily: T.sans, fontSize: 14, fontWeight: 500, color: T.ink, letterSpacing: -0.1 }}>{m.n}</div>
                <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50, marginTop: 2 }}>{m.v} videos</div>
              </div>
              <button onClick={onOpenSheet} style={{ ...iconBtn(T.brand) }}><I.eye/></button>
            </Card>
          ))}
        </div>

        {/* About */}
        <div style={{ padding: '20px 20px 0' }}>
          <SectionHead kicker="About" title="What you'll learn"/>
          <Card>
            <div style={{ fontFamily: T.sans, fontSize: 13, color: T.ink70, lineHeight: 1.6 }}>
              A clinically-oriented walkthrough of upper limb anatomy. Each lesson layers structure on function so you walk into your viva already explaining, not memorising.
            </div>
            <div style={{ marginTop: 14 }}>
              {[
                'Recognize bony landmarks on X-ray and cadaver',
                'Explain compartment syndromes and surgical approaches',
                'Localize brachial plexus injuries from clinical signs',
              ].map((b, i) => (
                <div key={i} style={{ display:'flex', gap: 10, paddingTop: i===0?0:8 }}>
                  <I.checkC style={{ color: T.brand, flexShrink:0, marginTop: 1 }}/>
                  <div style={{ fontFamily: T.sans, fontSize: 13, color: T.ink, lineHeight: 1.4 }}>{b}</div>
                </div>
              ))}
            </div>
          </Card>
        </div>

        {/* Rating */}
        <div style={{ padding: '20px 20px 0' }}>
          <Card style={{ display:'flex', alignItems:'center', gap: 14 }}>
            <div style={{ fontFamily: T.serif, fontSize: 42, color: T.ink, letterSpacing: -0.5, lineHeight: 1 }}>4.9</div>
            <div>
              <Stars value={5}/>
              <div style={{ fontFamily: T.sans, fontSize: 12, color: T.ink50, marginTop: 4 }}>248 student ratings</div>
            </div>
          </Card>
        </div>

        <div style={{ height: 28 }}/>
      </div>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Content Preview Bottom Sheet
// ──────────────────────────────────────────────────────────────
function PreviewSheet({ open, onClose }) {
  return (
    <>
      <div onClick={onClose} style={{
        position:'absolute', inset:0, zIndex: 90,
        background: open ? 'rgba(15,26,24,0.45)' : 'transparent',
        pointerEvents: open ? 'auto' : 'none', transition: 'background .2s',
      }}/>
      <div style={{
        position:'absolute', left: 0, right: 0, bottom: 0, zIndex: 95,
        background: T.surface, borderRadius: '24px 24px 0 0',
        transform: open ? 'translateY(0)' : 'translateY(100%)',
        transition: 'transform .28s cubic-bezier(.2,.7,.3,1)',
        boxShadow: T.shadowSheet, maxHeight: '76%', display:'flex', flexDirection:'column',
      }}>
        <div style={{ display:'flex', justifyContent:'center', padding: '8px 0 4px' }}>
          <div style={{ width: 40, height: 4, borderRadius: 4, background: T.ink10 }}/>
        </div>
        <div style={{ padding: '4px 20px 16px', borderBottom:`1px solid ${T.hair2}` }}>
          <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1, color: T.brand, textTransform:'uppercase', marginBottom: 6 }}>Course content</div>
          <div style={{ fontFamily: T.serif, fontSize: 22, color: T.ink, letterSpacing: -0.3 }}>Bones of the Upper Limb</div>
          <div style={{ fontFamily: T.sans, fontSize: 12, color: T.ink50, marginTop: 4 }}>7 video lessons · 1h 48m</div>
        </div>
        <div style={{ flex:1, overflow:'auto', padding: '8px 20px 32px' }}>
          {[
            'Clavicle & scapula', 'Humerus shaft', 'Elbow joint', 'Forearm bones',
            'Carpal arch', 'Bones in clinical X-ray', 'Practice questions',
          ].map((n, i) => (
            <div key={i} style={{ display:'flex', alignItems:'center', gap: 12, padding: '14px 0', borderBottom: i<6 ? `1px solid ${T.hair2}` : 'none' }}>
              <div style={{ width: 32, height: 32, borderRadius: 8, background: T.surface2, color: T.ink70, display:'flex', alignItems:'center', justifyContent:'center', fontFamily: T.serif, fontSize: 14 }}>
                {String(i+1).padStart(2,'0')}
              </div>
              <div style={{ flex:1 }}>
                <div style={{ fontFamily: T.sans, fontSize: 14, color: T.ink, fontWeight: 500 }}>{n}</div>
                <div style={{ display:'flex', alignItems:'center', gap: 6, marginTop: 4, fontFamily: T.sans, fontSize: 11, color: T.ink50 }}>
                  <I.play width={12} height={12}/> 1 video · {8+i} min
                </div>
              </div>
              <I.chev style={{ color: T.ink30 }}/>
            </div>
          ))}
        </div>
      </div>
    </>
  );
}

// ──────────────────────────────────────────────────────────────
// Articles tab
// ──────────────────────────────────────────────────────────────
function ArticlesScreen({ onOpenDrawer, onOpenArticle, onOpenCart }) {
  const arts = [
    { c: 'Clinical', t: 'How to think like a clinician in 90 seconds', d: 'A framework med students use to triage a vignette under exam pressure.', date: 'Mar 14', read: '6 min', tone: 'teal' },
    { c: 'Study', t: 'Spaced repetition beats massed cramming, here\'s why', d: 'A primer on Ebbinghaus, Anki, and how to apply it without burning out.', date: 'Mar 11', read: '8 min', tone: 'sand' },
    { c: 'Career', t: 'FCPS Part 1: a realistic 12-week plan', d: 'What to study, what to skip, and what really gets you the marks.', date: 'Mar 5', read: '12 min', tone: 'coral' },
  ];
  return (
    <Phone>
      <AppBar variant="default" onMenu={onOpenDrawer} onCart={onOpenCart} cartCount={2} onAvatar={()=>{}}/>
      <div style={{ flex:1, overflow:'auto', background: T.bg }}>
        <div style={{ padding: '20px 20px 16px' }}>
          <div style={{ fontFamily: T.serif, fontSize: 30, color: T.ink, letterSpacing: -0.3, lineHeight: 1.1 }}>Articles</div>
          <div style={{ fontFamily: T.sans, fontSize: 13, color: T.ink50, marginTop: 6 }}>Field notes from the wards & wards.</div>
        </div>
        <div style={{ padding: '0 20px 12px' }}>
          <SearchBar placeholder="Search articles…" trailing={
            <button style={{ width: 32, height: 32, borderRadius:'50%', background: T.brand, color:'#fff', border:0, display:'flex', alignItems:'center', justifyContent:'center', cursor:'pointer' }}>
              <I.send width={16} height={16}/>
            </button>
          }/>
        </div>
        <div style={{ display:'flex', gap: 8, padding: '4px 20px 16px', overflowX:'auto' }}>
          {['All', 'Clinical', 'Study', 'Career', 'Wellness', 'Research'].map((c, i) => (
            <Chip key={c} active={i===0} tone={i===0 ? 'brand' : 'ghost'}>{c}</Chip>
          ))}
        </div>

        <div style={{ padding: '0 20px' }}>
          {arts.map((a, i) => (
            <Card key={i} p={0} style={{ marginBottom: 14, overflow:'hidden' }} onClick={i===0 ? onOpenArticle : undefined}>
              <Placeholder h={160} tone={a.tone} label={a.t.toLowerCase()} radius={0}/>
              <div style={{ padding: 16 }}>
                <Chip tone="soft" style={{ marginBottom: 10, padding:'3px 10px', fontSize: 10, fontWeight: 700, letterSpacing: 0.5, textTransform:'uppercase' }}>{a.c}</Chip>
                <div style={{ fontFamily: T.serif, fontSize: 20, color: T.ink, lineHeight: 1.2, letterSpacing: -0.3, marginBottom: 6 }}>{a.t}</div>
                <div style={{ fontFamily: T.sans, fontSize: 13, color: T.ink50, lineHeight: 1.5, marginBottom: 12 }}>{a.d}</div>
                <div style={{ display:'flex', alignItems:'center', gap: 8, fontFamily: T.mono, fontSize: 10, color: T.ink50, letterSpacing: 0.5, textTransform:'uppercase' }}>
                  <span>{a.date}</span>
                  <span>·</span>
                  <I.clock width={12} height={12}/>
                  <span>{a.read} read</span>
                </div>
              </div>
            </Card>
          ))}
        </div>
        <NavSpacer/>
      </div>
      <BottomNav active="articles"/>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Article Detail
// ──────────────────────────────────────────────────────────────
function ArticleDetailScreen({ onBack }) {
  return (
    <Phone>
      <AppBar variant="logo" onBack={onBack} trailing={
        <div style={{ display:'flex', gap: 2 }}>
          <button style={iconBtn(T.ink)}><I.search/></button>
          <button style={iconBtn(T.ink)}><I.cart/></button>
        </div>
      }/>
      <div style={{ flex:1, overflow:'auto', background: T.surface }}>
        <div style={{ padding: '20px 20px 0' }}>
          <div style={{ display:'flex', gap: 6, marginBottom: 14 }}>
            <Chip tone="soft" style={{ padding: '3px 10px', fontSize: 10, fontWeight: 700, letterSpacing: 0.5, textTransform: 'uppercase' }}>Clinical</Chip>
            <Chip tone="coral" style={{ padding: '3px 10px', fontSize: 10, fontWeight: 700, letterSpacing: 0.5, textTransform: 'uppercase' }}>Featured</Chip>
          </div>
          <div style={{ fontFamily: T.serif, fontSize: 32, color: T.ink, lineHeight: 1.1, letterSpacing: -0.5, marginBottom: 16 }}>
            How to think like a clinician in 90 seconds
          </div>
          <div style={{ display:'flex', alignItems:'center', gap: 12, marginBottom: 20 }}>
            <Avatar name="Dr. Tanvir Hossain" size={36}/>
            <div>
              <div style={{ fontFamily: T.sans, fontSize: 13, fontWeight: 600, color: T.ink }}>Dr. Tanvir Hossain</div>
              <div style={{ fontFamily: T.mono, fontSize: 10, color: T.ink50, marginTop: 2, letterSpacing: 0.5, textTransform:'uppercase' }}>March 14, 2026 · 6 min read</div>
            </div>
          </div>
        </div>
        <Placeholder h={220} tone="teal" label="hero illustration" radius={0}/>
        <div style={{ padding: '20px 20px 0' }}>
          <div style={{ fontFamily: T.serif, fontSize: 19, fontStyle:'italic', color: T.ink70, lineHeight: 1.45, borderLeft: `3px solid ${T.brand}`, paddingLeft: 14, marginBottom: 20 }}>
            "Diagnosis isn't a guess at the answer. It's a structured argument that eliminates wrong ones."
          </div>
          <div style={{ fontFamily: T.sans, fontSize: 15, color: T.ink, lineHeight: 1.65, marginBottom: 16 }}>
            When a patient walks in with chest pain, the experienced clinician runs a silent decision tree in their head — not from memory, but from a learned <em>shape</em> of probability.
          </div>
          <div style={{ fontFamily: T.sans, fontSize: 15, color: T.ink, lineHeight: 1.65, marginBottom: 20 }}>
            That shape can be taught. Once you can describe what you're ruling <em>out</em> at each fork, you stop fishing through your knowledge and start applying it.
          </div>
          <Chip leading={<I.globe width={14} height={14}/>} tone="soft">বাংলায় অনুবাদ করুন</Chip>

          <div style={{ marginTop: 28 }}>
            <SectionHead kicker="Read next" title="Related articles"/>
            {[
              { c: 'Study', t: 'Spaced repetition beats cramming', d: 'Mar 11 · 8 min', tone: 'sand' },
              { c: 'Career', t: 'FCPS Part 1: a 12-week plan', d: 'Mar 5 · 12 min', tone: 'coral' },
            ].map((a, i) => (
              <Card key={i} p={0} style={{ marginBottom: 10, display:'flex', overflow:'hidden' }}>
                <Placeholder w={90} h={90} tone={a.tone} label={a.c} radius={0}/>
                <div style={{ padding: 12, flex:1 }}>
                  <Chip tone="soft" style={{ padding:'2px 8px', fontSize: 9, fontWeight: 700, letterSpacing: 0.4, textTransform:'uppercase' }}>{a.c}</Chip>
                  <div style={{ fontFamily: T.sans, fontSize: 13, fontWeight: 600, color: T.ink, marginTop: 6, lineHeight: 1.3 }}>{a.t}</div>
                  <div style={{ fontFamily: T.mono, fontSize: 10, color: T.ink50, marginTop: 4, letterSpacing: 0.5 }}>{a.d}</div>
                </div>
              </Card>
            ))}
          </div>
          <div style={{ textAlign:'center', padding: '24px 0 32px' }}>
            <div style={{ fontFamily: T.sans, fontSize: 13, color: T.brand, fontWeight: 600, display:'inline-flex', alignItems:'center', gap: 6 }}>
              View all articles <I.arrR width={14} height={14}/>
            </div>
          </div>
        </div>
      </div>
    </Phone>
  );
}

Object.assign(window, {
  SplashScreen, HomeScreen, CoursesScreen, CourseListScreen,
  CourseDetailScreen, PreviewSheet, ArticlesScreen, ArticleDetailScreen,
});
