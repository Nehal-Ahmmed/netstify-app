// system.jsx — design tokens, icons, primitives for BrainSton LMS

// ──────────────────────────────────────────────────────────────
// Tokens
// ──────────────────────────────────────────────────────────────
const T = {
  // Backgrounds
  bg:        '#F4F2EE',  // warm near-white app background (off-canvas)
  surface:   '#FFFFFF',  // cards
  surface2:  '#FAF8F4',  // subtle alt surface
  surfaceDk: '#0F1A18',  // deep teal-black for dark surfaces
  // Ink
  ink:       '#0F1A18',
  ink70:     '#3A4744',
  ink50:     '#6E7A77',
  ink30:     '#A6ADAB',
  ink10:     '#E6E4DF',
  // Brand — deep teal (MediShark)
  brand:     '#0E6E5A',
  brandDeep: '#0A4F41',
  brandSoft: '#E7F1EE',
  brandTint: '#F1F7F5',
  brandInk:  '#FFFFFF',
  // Accent — warm coral (sparingly: sale/heart/error)
  coral:     '#D97A57',
  coralSoft: '#FAEBE3',
  // Functional
  warn:      '#B8842B',
  warnSoft:  '#FAF1DE',
  ok:        '#1B7A53',
  okSoft:    '#DDEEE6',
  // Border / dividers
  hair:      'rgba(15,26,24,0.08)',
  hair2:     'rgba(15,26,24,0.05)',
  // Shadows
  shadowCard: '0 1px 0 rgba(15,26,24,0.04), 0 8px 24px -16px rgba(15,26,24,0.18)',
  shadowSheet: '0 -8px 32px -8px rgba(15,26,24,0.16)',
  shadowFab:  '0 6px 20px -6px rgba(14,110,90,0.45)',
  // Type
  sans:      "'Inter Tight', -apple-system, system-ui, sans-serif",
  serif:     "'Instrument Serif', Georgia, serif",
  mono:      "'JetBrains Mono', ui-monospace, monospace",
  // Radii
  r:         { xs: 6, s: 10, m: 14, l: 18, xl: 22, pill: 999 },
};

// ──────────────────────────────────────────────────────────────
// Icons (24px viewBox, stroke-based)
// ──────────────────────────────────────────────────────────────
const I = {
  menu: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" {...p}><path d="M4 7h16M4 12h16M4 17h10"/></svg>,
  back: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M15 5l-7 7 7 7"/></svg>,
  search: (p={}) => <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="1.7" {...p}><circle cx="11" cy="11" r="7"/><path d="M20 20l-3.5-3.5" strokeLinecap="round"/></svg>,
  cart: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M3 4h2.5l2 12.5a2 2 0 002 1.7h8.7a2 2 0 002-1.6L21.5 8H7"/><circle cx="10" cy="20.5" r="1.2"/><circle cx="18" cy="20.5" r="1.2"/></svg>,
  heart: (p={}) => <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinejoin="round" {...p}><path d="M12 20s-7-4.5-9.2-9.1C1.2 8 3 4.5 6.4 4.5c2 0 3.7 1.2 4.6 3 1-1.8 2.6-3 4.6-3 3.4 0 5.3 3.5 3.7 6.4C19 15.5 12 20 12 20z"/></svg>,
  heartF: (p={}) => <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor" {...p}><path d="M12 20s-7-4.5-9.2-9.1C1.2 8 3 4.5 6.4 4.5c2 0 3.7 1.2 4.6 3 1-1.8 2.6-3 4.6-3 3.4 0 5.3 3.5 3.7 6.4C19 15.5 12 20 12 20z"/></svg>,
  bell: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M6 16V11a6 6 0 1112 0v5l1.5 2h-15L6 16z"/><path d="M10 20a2 2 0 004 0"/></svg>,
  play: (p={}) => <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor" {...p}><path d="M8 5v14l11-7z"/></svg>,
  playC: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" strokeWidth="1.6" {...p}><circle cx="12" cy="12" r="9"/><path d="M10 8.5v7l6-3.5z" fill="currentColor" stroke="none"/></svg>,
  check: (p={}) => <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M5 12.5l4.5 4.5L19 7.5"/></svg>,
  checkC: (p={}) => <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor" {...p}><circle cx="12" cy="12" r="10"/><path d="M7.5 12.5l3 3 6-6.5" fill="none" stroke="#fff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>,
  chev: (p={}) => <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M9 6l6 6-6 6"/></svg>,
  chevD: (p={}) => <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M6 9l6 6 6-6"/></svg>,
  plus: (p={}) => <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" {...p}><path d="M12 5v14M5 12h14"/></svg>,
  x: (p={}) => <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" {...p}><path d="M6 6l12 12M18 6L6 18"/></svg>,
  trash: (p={}) => <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M4 7h16M9 7V4h6v3M6 7l1 13a2 2 0 002 2h6a2 2 0 002-2l1-13"/></svg>,
  user: (p={}) => <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="1.7" {...p}><circle cx="12" cy="8" r="4"/><path d="M4 21c0-4.4 3.6-8 8-8s8 3.6 8 8" strokeLinecap="round"/></svg>,
  star: (p={}) => <svg viewBox="0 0 24 24" width="14" height="14" fill="currentColor" {...p}><path d="M12 2l3.1 6.3 7 1-5 4.9 1.2 6.9L12 17.8 5.7 21l1.2-6.9-5-4.9 7-1z"/></svg>,
  starH: (p={}) => <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" strokeWidth="1.5" {...p}><path d="M12 2l3.1 6.3 7 1-5 4.9 1.2 6.9L12 17.8 5.7 21l1.2-6.9-5-4.9 7-1z"/></svg>,
  clock: (p={}) => <svg viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" strokeWidth="1.7" {...p}><circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2" strokeLinecap="round"/></svg>,
  book: (p={}) => <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M4 5a2 2 0 012-2h13v17H6a2 2 0 00-2 2V5z"/><path d="M8 7h7M8 11h7"/></svg>,
  home: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M4 11l8-7 8 7v9a1 1 0 01-1 1h-4v-6h-6v6H5a1 1 0 01-1-1v-9z"/></svg>,
  homeF: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor" {...p}><path d="M4 11l8-7 8 7v9a1 1 0 01-1 1h-4v-6h-6v6H5a1 1 0 01-1-1v-9z"/></svg>,
  grad: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M12 3l10 5-10 5L2 8l10-5z"/><path d="M6 10.5V15c0 2 3 3.5 6 3.5s6-1.5 6-3.5v-4.5"/></svg>,
  gradF: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor" {...p}><path d="M12 3l10 5-10 5L2 8l10-5zM6 10.5V15c0 2 3 3.5 6 3.5s6-1.5 6-3.5v-4.5l-6 3-6-3z"/></svg>,
  news: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" {...p}><rect x="3" y="5" width="14" height="14" rx="2"/><path d="M17 9h3v8a2 2 0 01-2 2"/><path d="M7 9h6M7 13h6M7 17h4"/></svg>,
  newsF: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor" {...p}><path d="M3 7a2 2 0 012-2h12a2 2 0 012 2v12a2 2 0 01-2 2H5a2 2 0 01-2-2V7zm4 2h8v2H7V9zm0 4h8v2H7v-2zm0 4h5v2H7v-2z"/></svg>,
  study: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M3 5h7a3 3 0 013 3v12a2 2 0 00-2-2H3V5zM21 5h-7a3 3 0 00-3 3v12a2 2 0 012-2h8V5z"/></svg>,
  studyF: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="currentColor" {...p}><path d="M3 5h7a3 3 0 013 3v12a2 2 0 00-2-2H3V5zM21 5h-7a3 3 0 00-3 3v12a2 2 0 012-2h8V5z"/></svg>,
  google: (p={}) => <svg viewBox="0 0 24 24" width="18" height="18" {...p}><path fill="#4285F4" d="M22 12.2c0-.7-.1-1.4-.2-2H12v3.8h5.6c-.2 1.3-1 2.4-2 3.1v2.5h3.3c1.9-1.8 3.1-4.4 3.1-7.4z"/><path fill="#34A853" d="M12 22c2.7 0 5-.9 6.7-2.4l-3.3-2.5c-.9.6-2 1-3.4 1-2.6 0-4.8-1.8-5.6-4.1H3v2.6A10 10 0 0012 22z"/><path fill="#FBBC05" d="M6.4 14a6 6 0 010-3.9V7.5H3a10 10 0 000 9l3.4-2.6z"/><path fill="#EA4335" d="M12 5.9c1.5 0 2.8.5 3.8 1.5l2.9-2.9A10 10 0 003 7.4l3.4 2.6C7.2 7.7 9.4 5.9 12 5.9z"/></svg>,
  arrR: (p={}) => <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M5 12h14M13 6l6 6-6 6"/></svg>,
  send: (p={}) => <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M3 12l18-9-7 18-3-7-8-2z"/></svg>,
  eye: (p={}) => <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.6" {...p}><path d="M1.5 12s4-7 10.5-7 10.5 7 10.5 7-4 7-10.5 7S1.5 12 1.5 12z"/><circle cx="12" cy="12" r="3"/></svg>,
  eyeOff: (p={}) => <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" {...p}><path d="M3 3l18 18M10.6 6.2A10 10 0 0112 6c6.5 0 10.5 7 10.5 7a17 17 0 01-3.4 4.2M6.2 8.2A17 17 0 001.5 13s4 7 10.5 7c1.7 0 3.2-.4 4.6-1"/><path d="M9.9 9.9a3 3 0 004.2 4.2"/></svg>,
  download: (p={}) => <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M12 3v12m0 0l-4-4m4 4l4-4M5 21h14"/></svg>,
  filter: (p={}) => <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M3 5h18l-7 9v6l-4-2v-4L3 5z"/></svg>,
  gear: (p={}) => <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="1.6" {...p}><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.7 1.7 0 00.3 1.8l.1.1a2 2 0 11-2.8 2.8l-.1-.1a1.7 1.7 0 00-1.8-.3 1.7 1.7 0 00-1 1.5V21a2 2 0 11-4 0v-.1a1.7 1.7 0 00-1.1-1.5 1.7 1.7 0 00-1.8.3l-.1.1a2 2 0 11-2.8-2.8l.1-.1a1.7 1.7 0 00.3-1.8 1.7 1.7 0 00-1.5-1H3a2 2 0 110-4h.1a1.7 1.7 0 001.5-1.1 1.7 1.7 0 00-.3-1.8l-.1-.1a2 2 0 112.8-2.8l.1.1a1.7 1.7 0 001.8.3h0a1.7 1.7 0 001-1.5V3a2 2 0 114 0v.1a1.7 1.7 0 001 1.5 1.7 1.7 0 001.8-.3l.1-.1a2 2 0 112.8 2.8l-.1.1a1.7 1.7 0 00-.3 1.8v0a1.7 1.7 0 001.5 1H21a2 2 0 110 4h-.1a1.7 1.7 0 00-1.5 1z"/></svg>,
  lock: (p={}) => <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" {...p}><rect x="4" y="11" width="16" height="10" rx="2"/><path d="M8 11V8a4 4 0 018 0v3"/></svg>,
  doc: (p={}) => <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M6 3h9l5 5v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5a2 2 0 012-2z"/><path d="M14 3v5h6"/></svg>,
  trophy: (p={}) => <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="1.7" strokeLinecap="round" strokeLinejoin="round" {...p}><path d="M8 4h8v5a4 4 0 11-8 0V4z"/><path d="M16 6h3v2a3 3 0 01-3 3M8 6H5v2a3 3 0 003 3M10 14h4v3h-4zM7 21h10"/></svg>,
  verify: (p={}) => <svg viewBox="0 0 16 16" width="14" height="14" fill="#0E6E5A" {...p}><path d="M8 1l1.5 1.4 2-.3.7 1.9 1.9.7-.3 2L15 8l-1.4 1.5.3 2-1.9.7-.7 1.9-2-.3L8 15l-1.5-1.4-2 .3-.7-1.9-1.9-.7.3-2L1 8l1.4-1.5-.3-2 1.9-.7L4.7 2l2 .3z"/><path d="M5.5 8l1.7 1.7L10.7 6" stroke="#fff" strokeWidth="1.5" fill="none" strokeLinecap="round" strokeLinejoin="round"/></svg>,
  globe: (p={}) => <svg viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="1.6" {...p}><circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c2.5 3 2.5 15 0 18M12 3c-2.5 3-2.5 15 0 18"/></svg>,
};

// ──────────────────────────────────────────────────────────────
// Reusable primitives
// ──────────────────────────────────────────────────────────────

// Soft striped image placeholder with monospace label
function Placeholder({ w='100%', h=160, label='image', tone='teal', radius=T.r.l, style={} }) {
  const tones = {
    teal: { a: '#E1ECE8', b: '#D6E5E0', ink: '#446B62' },
    sand: { a: '#EFE9DD', b: '#E5DDCD', ink: '#7A6A4D' },
    coral:{ a: '#F4DCD0', b: '#EDCEC0', ink: '#8A5340' },
    ink:  { a: '#1A2826', b: '#0F1A18', ink: '#94A8A4' },
    cool: { a: '#E2E8EB', b: '#D5DDE1', ink: '#4F5E64' },
  };
  const t = tones[tone] || tones.teal;
  return (
    <div style={{
      width: w, height: h, borderRadius: radius, overflow: 'hidden', position: 'relative',
      background: `repeating-linear-gradient(135deg, ${t.a} 0 14px, ${t.b} 14px 28px)`,
      ...style,
    }}>
      <div style={{
        position: 'absolute', inset: 0, display: 'flex', alignItems: 'flex-end',
        padding: 10, fontFamily: T.mono, fontSize: 10, color: t.ink, letterSpacing: 0.2,
        textTransform: 'uppercase',
      }}>{label}</div>
    </div>
  );
}

// Avatar with initials (deterministic color)
function Avatar({ name='AB', size=36, src, ring=false }) {
  const initials = (name || '?').split(' ').map(s => s[0]).slice(0,2).join('').toUpperCase();
  const palette = ['#0E6E5A', '#1F6F8B', '#8F5A3C', '#5A4A8F', '#3F7F4D', '#7F4A3F'];
  const idx = (name.charCodeAt(0) + (name.charCodeAt(1)||0)) % palette.length;
  const bg = palette[idx];
  if (src) {
    return <div style={{ width:size, height:size, borderRadius: '50%', background:src, backgroundSize:'cover', boxShadow: ring ? `0 0 0 2px #fff, 0 0 0 3.5px ${bg}` : 'none' }}/>;
  }
  return (
    <div style={{
      width: size, height: size, borderRadius: '50%', background: bg,
      color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center',
      fontFamily: T.sans, fontWeight: 600, fontSize: Math.round(size*0.38), letterSpacing: 0.3,
      boxShadow: ring ? `0 0 0 2px #fff, 0 0 0 3.5px ${bg}` : 'none', flexShrink: 0,
    }}>{initials}</div>
  );
}

// Brand mark
function BrandMark({ size=28, label=false, color=T.brand }) {
  return (
    <div style={{ display:'flex', alignItems:'center', gap: 8 }}>
      <div style={{ width: size, height: size, borderRadius: 8, background: color, position:'relative', display:'flex', alignItems:'center', justifyContent:'center' }}>
        <svg viewBox="0 0 24 24" width={size*0.62} height={size*0.62} fill="none" stroke="#fff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M9 4a4 4 0 00-4 4v1a3 3 0 00-1 5.6V17a3 3 0 003 3h1"/>
          <path d="M15 4a4 4 0 014 4v1a3 3 0 011 5.6V17a3 3 0 01-3 3h-1"/>
          <path d="M12 4v16"/>
        </svg>
      </div>
      {label && <div style={{ fontFamily: T.sans, fontSize: 17, fontWeight: 700, color: T.ink, letterSpacing: -0.3 }}>BrainSton</div>}
    </div>
  );
}

// Chip / pill / badge
function Chip({ children, active=false, leading, onClick, tone='default', style={} }) {
  const tones = {
    default: { bg: active ? T.ink : T.surface, fg: active ? '#fff' : T.ink70, border: active ? T.ink : T.hair },
    brand:   { bg: T.brand, fg: '#fff', border: T.brand },
    soft:    { bg: T.brandSoft, fg: T.brandDeep, border: 'transparent' },
    coral:   { bg: T.coralSoft, fg: T.coral, border: 'transparent' },
    warn:    { bg: T.warnSoft, fg: T.warn, border: 'transparent' },
    ok:      { bg: T.okSoft, fg: T.ok, border: 'transparent' },
    ghost:   { bg: 'transparent', fg: T.ink70, border: T.hair },
  };
  const t = tones[tone] || tones.default;
  return (
    <button onClick={onClick} style={{
      display:'inline-flex', alignItems:'center', gap: 6,
      padding: '7px 12px', borderRadius: T.r.pill, border: `1px solid ${t.border}`,
      background: t.bg, color: t.fg, fontFamily: T.sans, fontSize: 13, fontWeight: 500,
      cursor: onClick ? 'pointer' : 'default', whiteSpace: 'nowrap',
      ...style,
    }}>
      {leading}{children}
    </button>
  );
}

// Verified badge
function Verified({ size=14 }) {
  return <I.verify width={size} height={size}/>;
}

// Stars row
function Stars({ value=4.5, size=14, color=T.warn }) {
  const full = Math.floor(value);
  const half = value - full >= 0.5;
  return (
    <div style={{ display:'inline-flex', alignItems:'center', gap:1, color }}>
      {[...Array(5)].map((_,i) => {
        if (i < full) return <I.star key={i} width={size} height={size}/>;
        if (i === full && half) return <I.star key={i} width={size} height={size} style={{ opacity: 0.5 }}/>;
        return <I.starH key={i} width={size} height={size} style={{ color: T.ink30 }}/>;
      })}
    </div>
  );
}

// Button
function Btn({ children, variant='primary', size='md', leading, trailing, full=false, onClick, style={} }) {
  const variants = {
    primary: { bg: T.brand, fg: '#fff', border: 'transparent', hover: T.brandDeep },
    secondary: { bg: T.surface, fg: T.ink, border: T.hair, hover: T.surface2 },
    ghost: { bg: 'transparent', fg: T.ink, border: 'transparent', hover: T.surface2 },
    soft: { bg: T.brandSoft, fg: T.brandDeep, border: 'transparent', hover: T.brandTint },
    danger: { bg: T.coral, fg: '#fff', border: 'transparent', hover: '#C0664A' },
    dark: { bg: T.ink, fg: '#fff', border: 'transparent', hover: '#243430' },
  };
  const v = variants[variant];
  const sizes = {
    sm: { px: 12, py: 7, fs: 13, h: 32 },
    md: { px: 16, py: 10, fs: 14, h: 42 },
    lg: { px: 20, py: 14, fs: 15, h: 52 },
  };
  const s = sizes[size];
  return (
    <button onClick={onClick} style={{
      display:'inline-flex', alignItems:'center', justifyContent:'center', gap: 8,
      width: full ? '100%' : 'auto', height: s.h,
      padding: `0 ${s.px}px`, borderRadius: T.r.pill,
      background: v.bg, color: v.fg, border: `1px solid ${v.border}`,
      fontFamily: T.sans, fontSize: s.fs, fontWeight: 600, letterSpacing: -0.1,
      cursor: 'pointer', transition: 'all .15s', WebkitTapHighlightColor: 'transparent',
      ...style,
    }}
      onMouseEnter={e => e.currentTarget.style.background = v.hover}
      onMouseLeave={e => e.currentTarget.style.background = v.bg}
    >
      {leading}{children}{trailing}
    </button>
  );
}

// Progress bar
function Progress({ value=0.5, color=T.brand, track=T.ink10, h=6, label }) {
  return (
    <div style={{ width:'100%' }}>
      <div style={{ width:'100%', height:h, background:track, borderRadius:99, overflow:'hidden' }}>
        <div style={{ width:`${value*100}%`, height:'100%', background:color, borderRadius:99, transition:'width .35s' }}/>
      </div>
      {label && <div style={{ fontFamily:T.sans, fontSize:11, color:T.ink50, marginTop:4 }}>{label}</div>}
    </div>
  );
}

// Section header
function SectionHead({ title, action, kicker, style={} }) {
  return (
    <div style={{ display:'flex', alignItems:'flex-end', justifyContent:'space-between', marginBottom: 12, ...style }}>
      <div>
        {kicker && <div style={{ fontFamily:T.mono, fontSize:10, letterSpacing:1, color:T.ink50, textTransform:'uppercase', marginBottom:4 }}>{kicker}</div>}
        <div style={{ fontFamily:T.sans, fontSize:18, fontWeight:600, color:T.ink, letterSpacing:-0.3 }}>{title}</div>
      </div>
      {action && <div style={{ display:'flex', alignItems:'center', gap:4, fontFamily:T.sans, fontSize:13, color:T.brand, fontWeight:500, cursor:'pointer' }}>{action}<I.chev width={14} height={14}/></div>}
    </div>
  );
}

// Card
function Card({ children, p=16, style={}, onClick }) {
  return (
    <div onClick={onClick} style={{
      background: T.surface, borderRadius: T.r.l, padding: p,
      border: `1px solid ${T.hair2}`,
      cursor: onClick ? 'pointer' : 'default',
      ...style,
    }}>{children}</div>
  );
}

// Input
function Input({ label, value, onChange, placeholder, type='text', trailing, leading }) {
  const [focus, setFocus] = React.useState(false);
  return (
    <div>
      {label && <div style={{ fontFamily:T.sans, fontSize:12, fontWeight:500, color:T.ink70, marginBottom:6, letterSpacing:0.1 }}>{label}</div>}
      <div style={{
        display:'flex', alignItems:'center', gap:8,
        height: 48, padding: '0 14px',
        background: T.surface, borderRadius: T.r.m,
        border: `1.5px solid ${focus ? T.brand : T.hair}`,
        transition: 'border-color .12s',
      }}>
        {leading && <div style={{ color: T.ink50, display:'flex' }}>{leading}</div>}
        <input
          type={type}
          value={value || ''}
          onChange={e => onChange && onChange(e.target.value)}
          placeholder={placeholder}
          onFocus={() => setFocus(true)} onBlur={() => setFocus(false)}
          style={{ flex:1, border:0, outline:0, background:'transparent', fontFamily:T.sans, fontSize:15, color:T.ink, minWidth:0 }}
        />
        {trailing}
      </div>
    </div>
  );
}

// Phone status bar (light/dark) — uses iOS one
function PhoneStatus({ dark=false, time='9:41' }) {
  return <IOSStatusBar dark={dark} time={time}/>;
}

// Tab pill
function TabPill({ tabs, active, onChange }) {
  return (
    <div style={{ display:'flex', background:T.surface2, borderRadius: T.r.pill, padding: 4 }}>
      {tabs.map((t,i) => (
        <button key={t} onClick={() => onChange && onChange(i)} style={{
          flex:1, height: 36, border:0, background: active===i ? T.surface : 'transparent',
          borderRadius: T.r.pill, fontFamily:T.sans, fontSize:13, fontWeight: active===i ? 600 : 500,
          color: active===i ? T.ink : T.ink50, cursor:'pointer', transition:'all .15s',
          boxShadow: active===i ? T.shadowCard : 'none',
        }}>{t}</button>
      ))}
    </div>
  );
}

Object.assign(window, {
  T, I, Placeholder, Avatar, BrandMark, Chip, Verified, Stars, Btn, Progress,
  SectionHead, Card, Input, PhoneStatus, TabPill,
});
