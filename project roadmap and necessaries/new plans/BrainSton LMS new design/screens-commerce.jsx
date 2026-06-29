// screens-commerce.jsx — Cart, Checkout, Wishlist

function CartScreen({ onBack, onCheckout, empty=false, authed=true }) {
  if (!authed) return (
    <Phone>
      <AppBar variant="title" title="Cart" onBack={onBack} authed={false} onAvatar={()=>{}}/>
      <AuthBlock variant="cart"/>
    </Phone>
  );
  if (empty) return (
    <Phone>
      <AppBar variant="title" title="Cart" onBack={onBack} onAvatar={()=>{}}/>
      <div style={{ flex:1, padding: '40px 24px', display:'flex', flexDirection:'column', alignItems:'center', justifyContent:'center', textAlign:'center' }}>
        <div style={{ width: 96, height: 96, borderRadius: 28, background: T.brandSoft, color: T.brand, display:'flex', alignItems:'center', justifyContent:'center', marginBottom: 24 }}>
          <I.cart width={36} height={36}/>
        </div>
        <div style={{ fontFamily: T.serif, fontSize: 26, color: T.ink, letterSpacing: -0.3, marginBottom: 8 }}>Your cart is empty</div>
        <div style={{ fontFamily: T.sans, fontSize: 13, color: T.ink50, lineHeight: 1.5, maxWidth: 260, marginBottom: 22 }}>Browse the catalog and start a course that fits your year.</div>
        <Btn variant="primary" size="lg">Continue shopping</Btn>
      </div>
    </Phone>
  );

  const items = [
    { t: 'Anatomy of the Upper Limb', c: 'Year 1-2 · 28 lessons', price: 2400, was: 3200, tone: 'teal' },
    { t: 'Biochem: Carbohydrate Metabolism', c: 'Year 2 · 22 lessons', price: 1900, tone: 'sand' },
  ];
  const subtotal = items.reduce((s,i)=>s+i.price, 0);
  const original = items.reduce((s,i)=>s+(i.was||i.price), 0);
  const saved = original - subtotal;

  return (
    <Phone>
      <AppBar variant="title" title="Cart" onBack={onBack} onAvatar={()=>{}}/>
      <div style={{ flex:1, overflow:'auto', background: T.bg }}>
        <div style={{ padding: '16px 20px 0', display:'flex', alignItems:'center', justifyContent:'space-between' }}>
          <div style={{ fontFamily: T.sans, fontSize: 13, color: T.ink70 }}>{items.length} items · ৳{subtotal.toLocaleString()}</div>
          <button style={{ background:'none', border:0, color: T.coral, fontFamily: T.sans, fontSize: 12, fontWeight: 600, cursor:'pointer', display:'flex', alignItems:'center', gap: 4 }}>
            <I.trash width={14} height={14}/>Clear cart
          </button>
        </div>

        <div style={{ padding: '16px 20px 0' }}>
          <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1, color: T.ink50, textTransform:'uppercase', marginBottom: 10 }}>
            Course items ({items.length})
          </div>
          {items.map((it, i) => (
            <Card key={i} p={0} style={{ marginBottom: 12, overflow: 'hidden' }}>
              <div style={{ display:'flex' }}>
                <Placeholder w={92} h="auto" tone={it.tone} label="" radius={0} style={{ minHeight: 110 }}/>
                <div style={{ flex:1, padding: 14 }}>
                  <div style={{ display:'flex', justifyContent:'space-between', alignItems:'flex-start', gap: 8 }}>
                    <div style={{ flex:1 }}>
                      <div style={{ fontFamily: T.sans, fontSize: 14, fontWeight: 600, color: T.ink, lineHeight: 1.3 }}>{it.t}</div>
                      <Chip tone="soft" style={{ marginTop: 6, padding: '2px 8px', fontSize: 10, letterSpacing: 0.3 }}>{it.c}</Chip>
                    </div>
                    <button style={{ background:'none', border:0, color: T.ink50, cursor:'pointer', padding: 2 }}><I.trash/></button>
                  </div>
                  <div style={{ display:'flex', alignItems:'baseline', gap: 8, marginTop: 10 }}>
                    <div style={{ fontFamily: T.serif, fontSize: 18, color: T.ink, letterSpacing: -0.3 }}>৳{it.price.toLocaleString()}</div>
                    {it.was && <div style={{ fontFamily: T.sans, fontSize: 12, color: T.ink50, textDecoration:'line-through' }}>৳{it.was.toLocaleString()}</div>}
                    {it.was && <Chip tone="coral" style={{ padding:'1px 6px', fontSize: 9, fontWeight: 700 }}>-{Math.round((1-it.price/it.was)*100)}%</Chip>}
                  </div>
                </div>
              </div>
            </Card>
          ))}
        </div>

        {/* Promo */}
        <div style={{ padding: '8px 20px 0' }}>
          <Card style={{ background: T.warnSoft, border:'none', display:'flex', alignItems:'center', gap: 10 }}>
            <div style={{ width: 32, height: 32, borderRadius: 8, background: T.warn, color:'#fff', display:'flex', alignItems:'center', justifyContent:'center', fontFamily: T.serif, fontSize: 16 }}>%</div>
            <div style={{ flex:1, fontFamily: T.sans, fontSize: 12, color: T.ink70 }}>
              Add 1 more course to unlock <b>10% bundle discount</b>
            </div>
          </Card>
        </div>

        {/* Order Summary */}
        <div style={{ padding: '20px 20px 0' }}>
          <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1, color: T.ink50, textTransform:'uppercase', marginBottom: 10 }}>Order summary</div>
          <Card>
            {[
              { l: 'Subtotal', v: `৳${original.toLocaleString()}` },
              { l: 'Discount', v: `– ৳${saved.toLocaleString()}`, c: T.coral },
              { l: 'Platform fee', v: 'Free', c: T.ok },
            ].map((r, i) => (
              <div key={i} style={{ display:'flex', justifyContent:'space-between', padding: '6px 0', fontFamily: T.sans, fontSize: 13, color: r.c || T.ink70 }}>
                <span>{r.l}</span><span>{r.v}</span>
              </div>
            ))}
            <div style={{ borderTop: `1px solid ${T.hair2}`, marginTop: 8, paddingTop: 12, display:'flex', justifyContent:'space-between', alignItems:'baseline' }}>
              <span style={{ fontFamily: T.sans, fontSize: 13, color: T.ink70 }}>Total</span>
              <span style={{ fontFamily: T.serif, fontSize: 26, color: T.ink, letterSpacing: -0.3 }}>৳{subtotal.toLocaleString()}</span>
            </div>
          </Card>
        </div>

        <div style={{ height: 20 }}/>
      </div>
      {/* Sticky checkout */}
      <div style={{
        padding: '12px 20px 28px', background: T.surface,
        borderTop: `1px solid ${T.hair2}`, boxShadow: '0 -8px 24px -16px rgba(15,26,24,0.18)',
      }}>
        <Btn variant="primary" size="lg" full trailing={<I.arrR/>} onClick={onCheckout}>
          Proceed to checkout · ৳{subtotal.toLocaleString()}
        </Btn>
      </div>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Checkout
// ──────────────────────────────────────────────────────────────
function CheckoutScreen({ onBack }) {
  const [pay, setPay] = React.useState('aparspay');
  return (
    <Phone>
      <AppBar variant="title" title="Checkout" onBack={onBack} onAvatar={()=>{}} trailing={null}/>
      <div style={{ flex:1, overflow:'auto', background: T.bg }}>
        {/* Step indicator */}
        <div style={{ padding: '14px 20px 8px', display:'flex', alignItems:'center', gap: 8 }}>
          {['Cart', 'Billing', 'Pay'].map((s, i) => (
            <React.Fragment key={s}>
              <div style={{ display:'flex', alignItems:'center', gap: 6 }}>
                <div style={{ width: 22, height: 22, borderRadius:'50%', background: i<2 ? T.brand : T.ink10, color: i<2 ? '#fff' : T.ink50, display:'flex', alignItems:'center', justifyContent:'center', fontFamily: T.sans, fontSize: 11, fontWeight: 700 }}>{i+1}</div>
                <div style={{ fontFamily: T.sans, fontSize: 12, color: i<2 ? T.ink : T.ink50, fontWeight: i===1 ? 600 : 500 }}>{s}</div>
              </div>
              {i<2 && <div style={{ flex:1, height: 1, background: i<1 ? T.brand : T.ink10 }}/>}
            </React.Fragment>
          ))}
        </div>

        {/* Your order */}
        <div style={{ padding: '14px 20px 0' }}>
          <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1, color: T.ink50, textTransform:'uppercase', marginBottom: 10 }}>Your order (2)</div>
          <Card p={0}>
            {[
              { t: 'Anatomy of the Upper Limb', c: 'Year 1-2', p: 2400, tone: 'teal' },
              { t: 'Biochem: Carbohydrate Metabolism', c: 'Year 2', p: 1900, tone: 'sand' },
            ].map((it, i) => (
              <div key={i} style={{ display:'flex', gap: 12, padding: 12, borderTop: i===0 ? 'none' : `1px solid ${T.hair2}`, alignItems:'center' }}>
                <Placeholder w={48} h={48} tone={it.tone} label="" radius={10}/>
                <div style={{ flex:1 }}>
                  <div style={{ fontFamily: T.sans, fontSize: 13, fontWeight: 600, color: T.ink, lineHeight: 1.3 }}>{it.t}</div>
                  <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50, marginTop: 2 }}>{it.c}</div>
                </div>
                <div style={{ fontFamily: T.serif, fontSize: 16, color: T.ink, letterSpacing: -0.3 }}>৳{it.p.toLocaleString()}</div>
              </div>
            ))}
          </Card>
        </div>

        {/* Billing */}
        <div style={{ padding: '20px 20px 0' }}>
          <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1, color: T.ink50, textTransform:'uppercase', marginBottom: 10 }}>Billing</div>
          <Card>
            <div style={{ display:'flex', flexDirection:'column', gap: 14 }}>
              {/* Read-only fields */}
              {[
                { l: 'Full Name', v: 'Ayesha Rahman' },
                { l: 'Email', v: 'ayesha.r@dmc.edu' },
              ].map((f, i) => (
                <div key={i}>
                  <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50, marginBottom: 6, letterSpacing: 0.1 }}>{f.l}</div>
                  <div style={{ height: 46, padding: '0 14px', display:'flex', alignItems:'center', justifyContent:'space-between', background: T.surface2, borderRadius: T.r.m, fontFamily: T.sans, fontSize: 14, color: T.ink }}>
                    <span>{f.v}</span>
                    <I.lock width={14} height={14} style={{ color: T.ink30 }}/>
                  </div>
                </div>
              ))}
              {/* Editable phone */}
              <div>
                <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50, marginBottom: 6 }}>Phone number</div>
                <div style={{ display:'flex', gap: 8 }}>
                  <div style={{ display:'flex', alignItems:'center', gap: 6, padding: '0 12px', height: 46, background: T.surface, border: `1.5px solid ${T.hair}`, borderRadius: T.r.m, fontFamily: T.sans, fontSize: 14, color: T.ink, fontWeight: 500 }}>
                    <span style={{ fontSize: 18 }}>🇧🇩</span>+880
                    <I.chevD style={{ color: T.ink50 }}/>
                  </div>
                  <div style={{ flex:1, padding: '0 14px', height: 46, display:'flex', alignItems:'center', background: T.surface, border: `1.5px solid ${T.brand}`, borderRadius: T.r.m, fontFamily: T.sans, fontSize: 14, color: T.ink }}>
                    1712 345 678
                  </div>
                </div>
              </div>
            </div>
          </Card>
        </div>

        {/* Coupon */}
        <div style={{ padding: '20px 20px 0' }}>
          <div style={{ display:'flex', gap: 8 }}>
            <div style={{ flex:1, height: 46, padding: '0 14px', display:'flex', alignItems:'center', background: T.surface, border: `1.5px dashed ${T.hair}`, borderRadius: T.r.m, fontFamily: T.sans, fontSize: 14, color: T.ink50 }}>
              Coupon code
            </div>
            <Btn variant="secondary" size="md">Apply</Btn>
          </div>
        </div>

        {/* Order summary */}
        <div style={{ padding: '20px 20px 0' }}>
          <Card>
            {[
              { l: 'Subtotal', v: '৳4,300' },
              { l: 'Discount', v: '– ৳800', c: T.coral },
              { l: 'Coupon', v: '— Apply to save more' },
            ].map((r, i) => (
              <div key={i} style={{ display:'flex', justifyContent:'space-between', padding: '4px 0', fontFamily: T.sans, fontSize: 13, color: r.c || T.ink70 }}>
                <span>{r.l}</span><span>{r.v}</span>
              </div>
            ))}
            <div style={{ borderTop:`1px solid ${T.hair2}`, marginTop: 8, paddingTop: 12, display:'flex', justifyContent:'space-between', alignItems:'baseline' }}>
              <span style={{ fontFamily: T.sans, fontSize: 13, color: T.ink70 }}>Total</span>
              <span style={{ fontFamily: T.serif, fontSize: 26, color: T.ink, letterSpacing: -0.3 }}>৳3,500</span>
            </div>
          </Card>
        </div>

        {/* Payment method */}
        <div style={{ padding: '20px 20px 0' }}>
          <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1, color: T.ink50, textTransform:'uppercase', marginBottom: 10 }}>Payment method</div>
          {[
            { id: 'aparspay', name: 'AparsPay', desc: 'Cards, bKash, Nagad, Rocket', badge: 'POPULAR', tone: T.brand },
            { id: 'ssl',     name: 'SSLCommerz', desc: 'All major banks & wallets', badge: 'NEW',     tone: T.warn },
          ].map(p => (
            <div key={p.id} onClick={() => setPay(p.id)} style={{
              display:'flex', alignItems:'center', gap: 12, padding: 14,
              background: T.surface, borderRadius: T.r.m, marginBottom: 8,
              border: `1.5px solid ${pay===p.id ? T.brand : T.hair2}`, cursor:'pointer',
            }}>
              <div style={{ width: 22, height: 22, borderRadius:'50%', border: `1.5px solid ${pay===p.id ? T.brand : T.ink30}`, display:'flex', alignItems:'center', justifyContent:'center' }}>
                {pay===p.id && <div style={{ width:10, height:10, borderRadius:'50%', background: T.brand }}/>}
              </div>
              <div style={{ flex:1 }}>
                <div style={{ display:'flex', alignItems:'center', gap: 6 }}>
                  <div style={{ fontFamily: T.sans, fontSize: 14, fontWeight: 600, color: T.ink }}>{p.name}</div>
                  <span style={{ fontFamily: T.mono, fontSize: 9, color: p.tone, padding:'2px 6px', background: T.surface2, borderRadius: 4, letterSpacing: 0.5 }}>{p.badge}</span>
                </div>
                <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50, marginTop: 2 }}>{p.desc}</div>
              </div>
            </div>
          ))}
        </div>

        <div style={{ padding: '0 20px', textAlign:'center', fontFamily: T.sans, fontSize: 11, color: T.ink50, marginTop: 14, lineHeight: 1.5 }}>
          By continuing you agree to MediShark's <span style={{ color: T.brand, fontWeight: 500 }}>terms of service</span>.
        </div>
        <div style={{ height: 20 }}/>
      </div>

      {/* Sticky pay button */}
      <div style={{ padding: '12px 20px 28px', background: T.surface, borderTop: `1px solid ${T.hair2}` }}>
        <Btn variant="primary" size="lg" full trailing={<I.arrR/>}>Pay ৳3,500</Btn>
      </div>
    </Phone>
  );
}

// ──────────────────────────────────────────────────────────────
// Wishlist
// ──────────────────────────────────────────────────────────────
function WishlistScreen({ onBack, authed=true }) {
  if (!authed) return (
    <Phone>
      <AppBar variant="title" title="Wishlist" onBack={onBack} authed={false} onAvatar={()=>{}}/>
      <AuthBlock variant="wishlist"/>
    </Phone>
  );

  const items = [
    { t: 'Pharmacology Lab', s: 'FCPS Part 1 · 9 modules', tone: 'coral' },
    { t: 'Pathology: Inflammation', s: 'Year 2-3 · 6 modules', tone: 'sand' },
    { t: 'Cardio CME Refresher', s: 'GP · 4 modules', tone: 'teal' },
  ];
  return (
    <Phone>
      <AppBar variant="title" title="Wishlist" onBack={onBack} onAvatar={()=>{}}/>
      <div style={{ flex:1, overflow:'auto', background: T.bg }}>
        <div style={{ padding: '16px 20px 0', display:'flex', alignItems:'center', justifyContent:'space-between' }}>
          <div style={{ fontFamily: T.sans, fontSize: 13, color: T.ink70 }}>{items.length} saved courses</div>
          <Chip leading={<I.filter/>}>Newest</Chip>
        </div>

        <div style={{ padding: '14px 20px 0' }}>
          <div style={{ fontFamily: T.mono, fontSize: 10, letterSpacing: 1, color: T.ink50, textTransform:'uppercase', marginBottom: 10 }}>Course items</div>
          {items.map((it, i) => (
            <Card key={i} p={0} style={{ marginBottom: 12, overflow: 'hidden', display: 'flex' }}>
              <Placeholder w={92} h={92} tone={it.tone} label="" radius={0}/>
              <div style={{ flex:1, padding: 12, display:'flex', flexDirection:'column', justifyContent:'space-between' }}>
                <div>
                  <div style={{ fontFamily: T.sans, fontSize: 14, fontWeight: 600, color: T.ink, lineHeight: 1.3 }}>{it.t}</div>
                  <div style={{ fontFamily: T.sans, fontSize: 11, color: T.ink50, marginTop: 4 }}>{it.s}</div>
                </div>
                <div style={{ display:'flex', alignItems:'center', gap: 8 }}>
                  <Btn size="sm" variant="primary">View</Btn>
                  <div style={{ flex:1 }}/>
                  <button style={{ background:'none', border:0, color: T.coral, cursor:'pointer', padding: 6 }}><I.heartF/></button>
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

Object.assign(window, { CartScreen, CheckoutScreen, WishlistScreen });
