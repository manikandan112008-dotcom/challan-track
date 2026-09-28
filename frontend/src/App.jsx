import { useEffect, useMemo, useState } from 'react';
import {
  Activity, ArrowDownRight, ArrowRight, BadgeIndianRupee, Bell, CarFront,
  Check, CheckCircle2, ChevronDown, CircleAlert, ClipboardList, CreditCard,
  FilePlus2, FileText, Gauge, LayoutDashboard, LogOut, Menu, Plus, RefreshCw,
  Search, ShieldCheck, SlidersHorizontal, TriangleAlert, X,
} from 'lucide-react';
import { Link, NavLink, Route, Routes, useLocation, useNavigate, useParams } from 'react-router-dom';
import { api, loadWorkspace } from './api.js';

const currency = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', maximumFractionDigits: 0 });
const dateLabel = (value) => value ? new Date(`${String(value).slice(0, 10)}T00:00:00`).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }) : '—';
const timeLabel = (value) => value ? new Date(value).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' }) : '—';

const navigation = [
  { label: 'Overview', path: '/', icon: LayoutDashboard },
  { label: 'Vehicles', path: '/vehicles', icon: CarFront },
  { label: 'Violation types', path: '/violations', icon: TriangleAlert },
  { label: 'Create challan', path: '/challans/new', icon: FilePlus2 },
  { label: 'Challan register', path: '/challans', icon: ClipboardList },
  { label: 'Payments', path: '/payments', icon: CreditCard },
];

function App() {
  const [staff, setStaff] = useState(() => sessionStorage.getItem('challan-track-staff') || '');
  const [workspace, setWorkspace] = useState({ vehicles: [], violationTypes: [], challans: [], historyErrors: 0 });
  const [loading, setLoading] = useState(false);
  const [loadError, setLoadError] = useState('');
  const [reloadKey, setReloadKey] = useState(0);
  const [toast, setToast] = useState(null);
  const [mobileNav, setMobileNav] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    if (!staff) return undefined;
    let active = true;
    setLoading(true);
    setLoadError('');
    loadWorkspace()
      .then((data) => { if (active) setWorkspace(data); })
      .catch((error) => { if (active) setLoadError(error.message); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [staff, reloadKey]);

  useEffect(() => {
    if (!toast) return undefined;
    const timer = window.setTimeout(() => setToast(null), 4200);
    return () => window.clearTimeout(timer);
  }, [toast]);

  const refresh = () => setReloadKey((key) => key + 1);
  const notify = (message, kind = 'success') => setToast({ message, kind });
  const signIn = (name) => {
    const value = name.trim() || 'Duty officer';
    sessionStorage.setItem('challan-track-staff', value);
    setStaff(value);
  };
  const signOut = () => {
    sessionStorage.removeItem('challan-track-staff');
    setStaff('');
    navigate('/');
  };

  if (!staff) return <LoginPage onSignIn={signIn} />;

  const pageTitle = navigation.find((item) => item.path === location.pathname)?.label
    || (location.pathname.startsWith('/challans/') && location.pathname !== '/challans/new' ? 'Challan details' : 'Enforcement desk');

  return (
    <div className="app-frame">
      <aside className={`sidebar ${mobileNav ? 'sidebar-open' : ''}`}>
        <div className="brand-lockup">
          <div className="brand-mark"><ShieldCheck size={22} strokeWidth={2.2} /></div>
          <div><strong>Challan Track</strong><span>TRAFFIC ENFORCEMENT</span></div>
          <button className="icon-button sidebar-close" aria-label="Close navigation" onClick={() => setMobileNav(false)}><X size={19} /></button>
        </div>
        <div className="station-label">WORKSPACE</div>
        <nav className="side-nav" aria-label="Main navigation">
          {navigation.map(({ label, path, icon: Icon }) => (
            <NavLink key={path} to={path} end={path === '/'} onClick={() => setMobileNav(false)} className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
              <Icon size={18} strokeWidth={1.9} /><span>{label}</span>
              {label === 'Challan register' && workspace.challans.length > 0 && <span className="nav-count">{workspace.challans.length}</span>}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-bottom">
          <div className="service-status"><span className="status-dot" /><div><b>API connected</b><small>Local enforcement server</small></div></div>
          <div className="signed-user"><div className="avatar">{staff.slice(0, 1).toUpperCase()}</div><div className="signed-copy"><b>{staff}</b><small>Traffic officer</small></div><button className="icon-button logout-button" title="Sign out" onClick={signOut}><LogOut size={16} /></button></div>
        </div>
      </aside>

      {mobileNav && <button className="nav-scrim" aria-label="Close navigation" onClick={() => setMobileNav(false)} />}
      <main className="main-area">
        <header className="topbar">
          <div className="topbar-leading">
            <button className="icon-button mobile-menu" aria-label="Open navigation" onClick={() => setMobileNav(true)}><Menu size={20} /></button>
            <div className="crumb"><span>CHALLAN TRACK</span><b>/</b><strong>{pageTitle}</strong></div>
          </div>
          <div className="topbar-actions">
            <span className="today-label">{new Date().toLocaleDateString('en-IN', { weekday: 'short', day: '2-digit', month: 'short', year: 'numeric' })}</span>
            <button className="icon-button refresh-button" title="Refresh data" onClick={refresh}><RefreshCw size={17} /></button>
            <div className="top-user"><span className="avatar avatar-small">{staff.slice(0, 1).toUpperCase()}</span><span>{staff}</span><ChevronDown size={14} /></div>
          </div>
        </header>
        {loadError && <div className="global-error"><CircleAlert size={17} /><span>Could not load the API: {loadError}</span><button onClick={refresh}>Retry</button></div>}
        {workspace.historyErrors > 0 && !loadError && <div className="global-warning"><CircleAlert size={16} />Some vehicle challan histories could not be loaded. Register totals may be incomplete.</div>}
        <div className="page-content">
          {loading && !workspace.vehicles.length ? <LoadingState /> : (
            <Routes>
              <Route path="/" element={<Dashboard data={workspace} />} />
              <Route path="/vehicles" element={<VehiclesPage vehicles={workspace.vehicles} refresh={refresh} notify={notify} />} />
              <Route path="/violations" element={<ViolationTypesPage types={workspace.violationTypes} refresh={refresh} notify={notify} />} />
              <Route path="/challans/new" element={<CreateChallanPage vehicles={workspace.vehicles} types={workspace.violationTypes} refresh={refresh} notify={notify} />} />
              <Route path="/challans" element={<ChallanListPage challans={workspace.challans} />} />
              <Route path="/challans/:id" element={<ChallanDetailsPage challans={workspace.challans} />} />
              <Route path="/payments" element={<PaymentsPage challans={workspace.challans} refresh={refresh} notify={notify} />} />
              <Route path="*" element={<NotFound />} />
            </Routes>
          )}
        </div>
      </main>
      {toast && <div className={`toast toast-${toast.kind}`} role="status">{toast.kind === 'success' ? <CheckCircle2 size={19} /> : <CircleAlert size={19} />}{toast.message}<button aria-label="Dismiss notification" onClick={() => setToast(null)}><X size={16} /></button></div>}
    </div>
  );
}

function LoginPage({ onSignIn }) {
  const [name, setName] = useState('');
  return (
    <div className="login-screen">
      <div className="login-aside">
        <div className="login-brand"><div className="brand-mark"><ShieldCheck size={22} /></div><strong>Challan Track</strong></div>
        <div className="login-message"><span className="eyebrow">TRAFFIC ENFORCEMENT SYSTEM</span><h1>Every violation.<br /><em>Accounted for.</em></h1><p>A clear, reliable desk for vehicle records, digital challans and payment follow-through.</p></div>
        <div className="login-footer"><span className="login-orbit"><Activity size={17} /></span>Connected to local enforcement services</div>
      </div>
      <div className="login-panel">
        <form className="login-form" onSubmit={(event) => { event.preventDefault(); onSignIn(name); }}>
          <div className="login-mobile-brand"><div className="brand-mark"><ShieldCheck size={22} /></div><strong>Challan Track</strong></div>
          <span className="eyebrow">STAFF WORKSPACE</span><h2>Welcome to the desk</h2><p className="muted">Enter your display name to open this local workspace.</p>
          <label className="field-label" htmlFor="staff-name">Officer name</label>
          <input id="staff-name" autoFocus value={name} onChange={(event) => setName(event.target.value)} placeholder="e.g. A. Kumar" maxLength={60} />
          <button className="button button-primary login-submit" type="submit">Continue to dashboard <ArrowRight size={17} /></button>
          <p className="auth-note">This screen is a local workspace entry only. The current backend does not provide authentication.</p>
        </form>
      </div>
    </div>
  );
}

function Dashboard({ data }) {
  const { vehicles, challans, violationTypes } = data;
  const paid = challans.filter((item) => item.paymentStatus === 'PAID' || item.status === 'PAID').length;
  const pending = challans.length - paid;
  const recent = [...challans].sort((a, b) => String(b.violationDate).localeCompare(String(a.violationDate))).slice(0, 5);
  const violationStats = violationTypes.map((type) => ({
    ...type,
    count: challans.filter((challan) => challan.violationType === type.violationName).length,
  })).sort((a, b) => b.count - a.count).slice(0, 5);
  const maxViolation = Math.max(1, ...violationStats.map((item) => item.count));

  return (
    <>
      <PageHeading eyebrow="COMMAND OVERVIEW" title="Good day, officer" description="A live view of your vehicle and challan register." action={<Link className="button button-primary" to="/challans/new"><Plus size={17} /> Issue a challan</Link>} />
      <section className="metric-grid" aria-label="Challan summary">
        <MetricCard label="Registered vehicles" value={vehicles.length} icon={CarFront} tone="mint" note="In the vehicle register" />
        <MetricCard label="Total challans" value={challans.length} icon={FileText} tone="blue" note="Across all vehicle records" />
        <MetricCard label="Pending payment" value={pending} icon={BadgeIndianRupee} tone="amber" note="Awaiting settlement" />
        <MetricCard label="Paid challans" value={paid} icon={CheckCircle2} tone="green" note="Payment recorded" />
      </section>
      <section className="dashboard-grid">
        <div className="surface recent-panel">
          <div className="section-heading"><div><span className="eyebrow">LATEST ACTIVITY</span><h2>Recent challans</h2></div><Link className="text-link" to="/challans">View register <ArrowRight size={15} /></Link></div>
          {recent.length ? <ChallanTable challans={recent} compact /> : <EmptyState icon={FileText} title="No challans yet" detail="Issued challans will appear here." action={<Link className="button button-secondary" to="/challans/new">Create first challan</Link>} />}
        </div>
        <div className="surface stats-panel">
          <div className="section-heading"><div><span className="eyebrow">ENFORCEMENT MIX</span><h2>Violation statistics</h2></div><span className="mini-icon"><SlidersHorizontal size={16} /></span></div>
          {violationStats.length ? <div className="violation-bars">{violationStats.map((item, index) => <div className="bar-row" key={item.id}><div className="bar-label"><span>{item.violationName}</span><b>{item.count}</b></div><div className="bar-track"><span className={`bar-fill bar-fill-${index % 4}`} style={{ width: `${Math.max(item.count ? 8 : 0, (item.count / maxViolation) * 100)}%` }} /></div></div>)}</div> : <EmptyState icon={TriangleAlert} title="No violation data" detail="Add violation types to start tracking." />}
          <Link className="text-link stats-link" to="/violations">Manage violation types <ArrowRight size={15} /></Link>
        </div>
      </section>
      <div className="data-note"><Activity size={15} />Dashboard counts are assembled from the backend vehicle and per-vehicle challan endpoints.</div>
    </>
  );
}

function MetricCard({ label, value, icon: Icon, tone, note }) {
  return <article className="metric-card"><div className={`metric-icon metric-${tone}`}><Icon size={19} /></div><span className="metric-label">{label}</span><strong className="metric-value">{value.toLocaleString('en-IN')}</strong><span className="metric-note">{note}</span></article>;
}

function VehiclesPage({ vehicles, refresh, notify }) {
  const [search, setSearch] = useState('');
  const [editing, setEditing] = useState(null);
  const filtered = vehicles.filter((vehicle) => `${vehicle.vehicleNumber} ${vehicle.ownerName} ${vehicle.vehicleType} ${vehicle.ownerPhone}`.toLowerCase().includes(search.toLowerCase()));
  const save = async (form) => {
    try {
      if (editing?.id) await api.updateVehicle(editing.id, form);
      else await api.createVehicle(form);
      setEditing(null); refresh(); notify(editing?.id ? 'Vehicle details updated.' : 'Vehicle added to the register.');
    } catch (error) { notify(error.message, 'error'); }
  };
  const remove = async (vehicle) => {
    if (!window.confirm(`Delete vehicle ${vehicle.vehicleNumber}?`)) return;
    try { await api.deleteVehicle(vehicle.id); refresh(); notify('Vehicle deleted.'); }
    catch (error) { notify(error.message, 'error'); }
  };
  return <>
    <PageHeading eyebrow="VEHICLE REGISTER" title="Vehicles" description={`${vehicles.length} registered vehicles`} action={<button className="button button-primary" onClick={() => setEditing({})}><Plus size={17} /> Add vehicle</button>} />
    <div className="surface table-surface">
      <div className="table-toolbar"><label className="search-box"><Search size={17} /><input aria-label="Search vehicles" placeholder="Search number, owner or type" value={search} onChange={(event) => setSearch(event.target.value)} />{search && <button aria-label="Clear search" onClick={() => setSearch('')}><X size={15} /></button>}</label><span className="record-count">{filtered.length} records</span></div>
      {filtered.length ? <div className="table-scroll"><table><thead><tr><th>Vehicle</th><th>Owner</th><th>Type</th><th>Contact</th><th>Registered</th><th><span className="sr-only">Actions</span></th></tr></thead><tbody>{filtered.map((vehicle) => <tr key={vehicle.id}><td><div className="vehicle-id"><span className="plate-icon"><CarFront size={16} /></span><strong>{vehicle.vehicleNumber}</strong></div></td><td>{vehicle.ownerName}</td><td><span className="type-pill">{vehicle.vehicleType}</span></td><td>{vehicle.ownerPhone}</td><td>{timeLabel(vehicle.createdAt)}</td><td><div className="row-actions"><button className="small-action" onClick={() => setEditing(vehicle)}>Edit</button><button className="small-action action-danger" onClick={() => remove(vehicle)}>Delete</button></div></td></tr>)}</tbody></table></div> : <EmptyState icon={CarFront} title={search ? 'No matching vehicles' : 'Vehicle register is empty'} detail={search ? 'Try a different number or owner name.' : 'Add a vehicle to begin issuing challans.'} action={!search && <button className="button button-secondary" onClick={() => setEditing({})}>Add vehicle</button>} />}
    </div>
    <VehicleModal vehicle={editing} onClose={() => setEditing(null)} onSave={save} />
  </>;
}

function VehicleModal({ vehicle, onClose, onSave }) {
  const [form, setForm] = useState({ vehicleNumber: '', ownerName: '', vehicleType: 'Car', ownerPhone: '' });
  const [saving, setSaving] = useState(false);
  useEffect(() => { if (vehicle) setForm({ vehicleNumber: vehicle.vehicleNumber || '', ownerName: vehicle.ownerName || '', vehicleType: vehicle.vehicleType || 'Car', ownerPhone: vehicle.ownerPhone || '' }); }, [vehicle]);
  if (!vehicle) return null;
  const update = (key, value) => setForm((previous) => ({ ...previous, [key]: value }));
  return <Modal title={vehicle.id ? 'Edit vehicle' : 'Add vehicle'} onClose={onClose}>
    <form className="form-grid" onSubmit={async (event) => { event.preventDefault(); setSaving(true); await onSave({ ...form, vehicleNumber: form.vehicleNumber.toUpperCase().replace(/\s/g, '') }); setSaving(false); }}>
      <Field label="Vehicle registration" hint="Format: TN01AB1234"><input required pattern="[A-Za-z]{2}[0-9]{2}[A-Za-z]{2}[0-9]{4}" title="Enter registration like TN01AB1234" value={form.vehicleNumber} onChange={(event) => update('vehicleNumber', event.target.value)} placeholder="TN01AB1234" /></Field>
      <Field label="Owner name"><input required maxLength={100} value={form.ownerName} onChange={(event) => update('ownerName', event.target.value)} placeholder="Full name" /></Field>
      <Field label="Vehicle type"><select value={form.vehicleType} onChange={(event) => update('vehicleType', event.target.value)}><option>Car</option><option>Bike</option><option>Truck</option><option>Bus</option><option>Auto</option><option>Other</option></select></Field>
      <Field label="Owner phone"><input required pattern="[0-9]{10}" title="Enter a 10-digit phone number" inputMode="numeric" value={form.ownerPhone} onChange={(event) => update('ownerPhone', event.target.value.replace(/\D/g, '').slice(0, 10))} placeholder="10-digit mobile number" /></Field>
      <FormActions onCancel={onClose} saving={saving} saveLabel={vehicle.id ? 'Save changes' : 'Add vehicle'} />
    </form>
  </Modal>;
}

function ViolationTypesPage({ types, refresh, notify }) {
  const [editing, setEditing] = useState(null);
  const [search, setSearch] = useState('');
  const filtered = types.filter((type) => `${type.violationCode} ${type.violationName} ${type.description || ''}`.toLowerCase().includes(search.toLowerCase()));
  const save = async (form) => {
    try {
      if (editing?.id) await api.updateViolationType(editing.id, form);
      else await api.createViolationType(form);
      setEditing(null); refresh(); notify(editing?.id ? 'Violation type updated.' : 'Violation type created.');
    } catch (error) { notify(error.message, 'error'); }
  };
  const remove = async (type) => {
    if (!window.confirm(`Delete ${type.violationName}?`)) return;
    try { await api.deleteViolationType(type.id); refresh(); notify('Violation type deleted.'); }
    catch (error) { notify(error.message, 'error'); }
  };
  return <>
    <PageHeading eyebrow="OFFENCE CATALOGUE" title="Violation types" description="Fine schedules and violation definitions" action={<button className="button button-primary" onClick={() => setEditing({})}><Plus size={17} /> Add violation</button>} />
    <div className="surface table-surface">
      <div className="table-toolbar"><label className="search-box"><Search size={17} /><input aria-label="Search violation types" placeholder="Search code or violation" value={search} onChange={(event) => setSearch(event.target.value)} /></label><span className="record-count">{filtered.length} types</span></div>
      {filtered.length ? <div className="table-scroll"><table><thead><tr><th>Code</th><th>Violation</th><th>Description</th><th>Base fine</th><th>Created</th><th><span className="sr-only">Actions</span></th></tr></thead><tbody>{filtered.map((type) => <tr key={type.id}><td><span className="code-label">{type.violationCode}</span></td><td><strong>{type.violationName}</strong></td><td className="description-cell">{type.description || '—'}</td><td className="money-cell">{currency.format(type.baseFine)}</td><td>{timeLabel(type.createdAt)}</td><td><div className="row-actions"><button className="small-action" onClick={() => setEditing(type)}>Edit</button><button className="small-action action-danger" onClick={() => remove(type)}>Delete</button></div></td></tr>)}</tbody></table></div> : <EmptyState icon={TriangleAlert} title="No violation types found" detail="Add a violation definition to make it available when issuing challans." action={<button className="button button-secondary" onClick={() => setEditing({})}>Add violation type</button>} />}
    </div>
    <ViolationModal type={editing} onClose={() => setEditing(null)} onSave={save} />
  </>;
}

function ViolationModal({ type, onClose, onSave }) {
  const [form, setForm] = useState({ violationCode: '', violationName: '', description: '', baseFine: '' });
  const [saving, setSaving] = useState(false);
  useEffect(() => { if (type) setForm({ violationCode: type.violationCode || '', violationName: type.violationName || '', description: type.description || '', baseFine: type.baseFine ?? '' }); }, [type]);
  if (!type) return null;
  const update = (key, value) => setForm((previous) => ({ ...previous, [key]: value }));
  return <Modal title={type.id ? 'Edit violation type' : 'Add violation type'} onClose={onClose}>
    <form className="form-grid" onSubmit={async (event) => { event.preventDefault(); setSaving(true); await onSave({ ...form, baseFine: Number(form.baseFine) }); setSaving(false); }}>
      <Field label="Violation code"><input required maxLength={30} value={form.violationCode} onChange={(event) => update('violationCode', event.target.value.toUpperCase())} placeholder="SPEED001" /></Field>
      <Field label="Violation name"><input required maxLength={100} value={form.violationName} onChange={(event) => update('violationName', event.target.value)} placeholder="Speeding" /></Field>
      <Field label="Base fine (INR)"><input required type="number" min="1" step="0.01" value={form.baseFine} onChange={(event) => update('baseFine', event.target.value)} placeholder="1000" /></Field>
      <Field label="Description" className="field-wide"><textarea rows="3" maxLength={255} value={form.description} onChange={(event) => update('description', event.target.value)} placeholder="Brief description of the violation" /></Field>
      <FormActions onCancel={onClose} saving={saving} saveLabel={type.id ? 'Save changes' : 'Add violation'} />
    </form>
  </Modal>;
}

function CreateChallanPage({ vehicles, types, refresh, notify }) {
  const navigate = useNavigate();
  const [vehicleId, setVehicleId] = useState('');
  const [typeId, setTypeId] = useState('');
  const [search, setSearch] = useState('');
  const [officerName, setOfficerName] = useState(sessionStorage.getItem('challan-track-staff') || '');
  const [violationDate, setViolationDate] = useState(new Date().toISOString().slice(0, 10));
  const [saving, setSaving] = useState(false);
  const selectedType = types.find((type) => String(type.id) === typeId);
  const filteredVehicles = vehicles.filter((vehicle) => `${vehicle.vehicleNumber} ${vehicle.ownerName}`.toLowerCase().includes(search.toLowerCase()));
  const submit = async (event) => {
    event.preventDefault(); setSaving(true);
    try {
      const created = await api.createChallan({ vehicleId: Number(vehicleId), violationTypeId: Number(typeId), officerName, violationDate });
      refresh(); notify(`Challan ${created.challanNumber} issued successfully.`); navigate(`/challans/${created.id}`);
    } catch (error) { notify(error.message, 'error'); }
    finally { setSaving(false); }
  };
  return <>
    <PageHeading eyebrow="NEW ENFORCEMENT RECORD" title="Issue a challan" description="Record the violation details and calculate the applicable fine." />
    <div className="create-layout">
      <form className="surface challan-form" onSubmit={submit}>
        <div className="form-section-title"><span className="step-number">01</span><div><h2>Violation details</h2><p>Choose the registered vehicle and applicable offence.</p></div></div>
        <Field label="Vehicle" hint="Search by registration number or owner"><div className="select-search"><Search size={16} /><input value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Search registered vehicles" /></div><select required value={vehicleId} onChange={(event) => setVehicleId(event.target.value)}><option value="">Select a vehicle</option>{filteredVehicles.map((vehicle) => <option key={vehicle.id} value={vehicle.id}>{vehicle.vehicleNumber} · {vehicle.ownerName}</option>)}</select></Field>
        <Field label="Violation type"><select required value={typeId} onChange={(event) => setTypeId(event.target.value)}><option value="">Select a violation</option>{types.map((type) => <option key={type.id} value={type.id}>{type.violationCode} · {type.violationName}</option>)}</select></Field>
        <div className="selected-fine"><div className="fine-icon"><BadgeIndianRupee size={19} /></div><div><span>BASE FINE</span><strong>{selectedType ? currency.format(selectedType.baseFine) : 'Select a violation'}</strong></div><small>Final fine may increase for repeat offences.</small></div>
        <div className="form-section-title second-step"><span className="step-number">02</span><div><h2>Issue information</h2><p>Identify the officer and date of the violation.</p></div></div>
        <div className="form-grid"><Field label="Issuing officer"><input required maxLength={100} value={officerName} onChange={(event) => setOfficerName(event.target.value)} placeholder="Officer name" /></Field><Field label="Violation date"><input required type="date" max={new Date().toISOString().slice(0, 10)} value={violationDate} onChange={(event) => setViolationDate(event.target.value)} /></Field></div>
        <div className="form-submit-row"><span><ShieldCheck size={16} /> Challan will be saved to the enforcement register</span><button className="button button-primary" type="submit" disabled={saving || !vehicles.length || !types.length}>{saving ? <Spinner small /> : <FilePlus2 size={17} />}{saving ? 'Issuing…' : 'Issue challan'}</button></div>
      </form>
      <aside className="create-aside"><div className="aside-symbol"><FileText size={22} /></div><span className="eyebrow">BEFORE YOU ISSUE</span><h3>Check the record</h3><p>Repeat offences are tracked per vehicle and violation type over a rolling 12-month period.</p><div className="aside-divider" /><div className="aside-fact"><b>{vehicles.length}</b><span>vehicles available</span></div><div className="aside-fact"><b>{types.length}</b><span>active violation types</span></div>{(!vehicles.length || !types.length) && <Link className="text-link" to={!vehicles.length ? '/vehicles' : '/violations'}>Add missing records <ArrowRight size={15} /></Link>}</aside>
    </div>
  </>;
}

function ChallanListPage({ challans }) {
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('ALL');
  const [violation, setViolation] = useState('ALL');
  const violationNames = [...new Set(challans.map((item) => item.violationType))].sort();
  const filtered = challans.filter((item) => {
    const matchesSearch = `${item.vehicleNumber} ${item.challanNumber} ${item.officerName}`.toLowerCase().includes(search.toLowerCase());
    const matchesStatus = status === 'ALL' || (item.paymentStatus || item.status) === status;
    const matchesViolation = violation === 'ALL' || item.violationType === violation;
    return matchesSearch && matchesStatus && matchesViolation;
  }).sort((a, b) => String(b.violationDate).localeCompare(String(a.violationDate)));
  return <>
    <PageHeading eyebrow="ENFORCEMENT RECORDS" title="Challan register" description={`${challans.length} challans across registered vehicles`} action={<Link className="button button-primary" to="/challans/new"><Plus size={17} /> Issue challan</Link>} />
    <div className="surface table-surface">
      <div className="table-toolbar filter-toolbar"><label className="search-box"><Search size={17} /><input aria-label="Search challans" placeholder="Search vehicle or challan no." value={search} onChange={(event) => setSearch(event.target.value)} /></label><div className="filter-control"><SlidersHorizontal size={16} /><select aria-label="Filter by payment status" value={status} onChange={(event) => setStatus(event.target.value)}><option value="ALL">All statuses</option><option value="UNPAID">Pending</option><option value="PAID">Paid</option></select></div><div className="filter-control"><select aria-label="Filter by violation" value={violation} onChange={(event) => setViolation(event.target.value)}><option value="ALL">All violations</option>{violationNames.map((name) => <option key={name} value={name}>{name}</option>)}</select></div><span className="record-count">{filtered.length} shown</span></div>
      {filtered.length ? <ChallanTable challans={filtered} /> : <EmptyState icon={ClipboardList} title={challans.length ? 'No matching challans' : 'No challans have been issued'} detail={challans.length ? 'Adjust your search or filters.' : 'New challans will appear here after they are issued.'} action={!challans.length && <Link className="button button-secondary" to="/challans/new">Issue a challan</Link>} />}
    </div>
    <div className="data-note"><CircleAlert size={15} />The backend exposes challan history per vehicle; this register combines those responses.</div>
  </>;
}

function ChallanTable({ challans, compact = false }) {
  return <div className={`table-scroll ${compact ? 'compact-table' : ''}`}><table><thead><tr><th>Challan</th><th>Vehicle</th><th>Violation</th><th>Date</th><th>Fine</th><th>Status</th>{!compact && <th><span className="sr-only">Details</span></th>}</tr></thead><tbody>{challans.map((item) => <tr key={item.id}><td><Link className="challan-link" to={`/challans/${item.id}`}>{item.challanNumber}</Link></td><td className="plate-cell">{item.vehicleNumber}</td><td>{item.violationType}</td><td>{dateLabel(item.violationDate)}</td><td className="money-cell">{currency.format(item.applicableFine)}</td><td><StatusPill status={item.paymentStatus || item.status} /></td>{!compact && <td><Link className="icon-button details-button" aria-label={`View ${item.challanNumber}`} to={`/challans/${item.id}`}><ArrowRight size={16} /></Link></td>}</tr>)}</tbody></table></div>;
}

function ChallanDetailsPage({ challans }) {
  const { id } = useParams();
  const challan = challans.find((item) => String(item.id) === id);
  if (!challan) return <><PageHeading eyebrow="CHALLAN RECORD" title="Details unavailable" description="This challan is not in the loaded vehicle histories." /><EmptyState icon={FileText} title="Record not found" detail="Return to the challan register and choose a loaded record." action={<Link className="button button-secondary" to="/challans">Back to register</Link>} /></>;
  return <>
    <div className="detail-back"><Link to="/challans"><ArrowDownRight size={16} /> Back to challan register</Link></div>
    <PageHeading eyebrow="CHALLAN RECORD" title={challan.challanNumber} description={`${challan.vehicleNumber} · ${challan.violationType}`} action={<StatusPill status={challan.paymentStatus || challan.status} />} />
    <div className="detail-grid"><section className="surface detail-card"><div className="section-heading"><div><span className="eyebrow">ISSUE SUMMARY</span><h2>Challan details</h2></div><span className="mini-icon"><FileText size={17} /></span></div><DetailRow label="Challan number" value={challan.challanNumber} /><DetailRow label="Vehicle registration" value={challan.vehicleNumber} /><DetailRow label="Violation" value={challan.violationType} /><DetailRow label="Issuing officer" value={challan.officerName || '—'} /><DetailRow label="Violation date" value={dateLabel(challan.violationDate)} /><DetailRow label="Payment status" value={<StatusPill status={challan.paymentStatus || challan.status} />} /></section>
      <section className="surface detail-card fine-summary"><span className="eyebrow">FINE SUMMARY</span><h2>{currency.format(challan.applicableFine)}</h2><p>Applicable fine</p><div className="fine-breakdown"><span>Base fine <b>{currency.format(challan.originalFine)}</b></span><span>Applicable fine <b>{currency.format(challan.applicableFine)}</b></span></div>{(challan.paymentStatus || challan.status) !== 'PAID' && <Link className="button button-primary button-full" to={`/payments?challan=${challan.id}`}>Record payment <ArrowRight size={16} /></Link>}</section></div>
  </>;
}

function PaymentsPage({ challans, refresh, notify }) {
  const [selected, setSelected] = useState(null);
  const [query] = useState(() => new URLSearchParams(window.location.search).get('challan'));
  const paid = challans.filter((item) => item.paymentStatus === 'PAID' || item.status === 'PAID');
  const pending = challans.filter((item) => item.paymentStatus !== 'PAID' && item.status !== 'PAID');
  useEffect(() => { if (query) { const match = pending.find((item) => String(item.id) === query); if (match) setSelected(match); } }, [query, pending]);
  const recordPayment = async (form) => {
    try {
      const payment = await api.createPayment(form);
      setSelected(null); refresh(); notify(`Payment ${payment.paymentReference} recorded.`);
    } catch (error) { notify(error.message, 'error'); }
  };
  return <>
    <PageHeading eyebrow="PAYMENT DESK" title="Payments" description="Record payment against an unpaid challan." />
    <div className="payment-summary-row"><div className="payment-summary surface"><div className="metric-icon metric-green"><Check size={18} /></div><span>Paid challans</span><strong>{paid.length}</strong></div><div className="payment-summary surface"><div className="metric-icon metric-amber"><BadgeIndianRupee size={18} /></div><span>Awaiting payment</span><strong>{pending.length}</strong></div></div>
    <section className="surface table-surface payment-panel"><div className="section-heading payment-heading"><div><span className="eyebrow">PAYMENT FOLLOW-UP</span><h2>Unpaid challans</h2></div><span className="record-count">{pending.length} pending</span></div>
      {pending.length ? <div className="table-scroll"><table><thead><tr><th>Challan</th><th>Vehicle</th><th>Violation</th><th>Issued</th><th>Amount due</th><th>Action</th></tr></thead><tbody>{pending.map((item) => <tr key={item.id}><td><Link className="challan-link" to={`/challans/${item.id}`}>{item.challanNumber}</Link></td><td className="plate-cell">{item.vehicleNumber}</td><td>{item.violationType}</td><td>{dateLabel(item.violationDate)}</td><td className="money-cell">{currency.format(item.applicableFine)}</td><td><button className="button button-small button-secondary" onClick={() => setSelected(item)}>Record payment <ArrowRight size={14} /></button></td></tr>)}</tbody></table></div> : <EmptyState icon={CheckCircle2} title="All caught up" detail="There are no unpaid challans in the loaded register." />}
    </section>
    <section className="surface payment-info"><div className="mini-icon"><CircleAlert size={17} /></div><div><b>Payment history availability</b><p>The backend supports payment submission and returns the created payment, but it has no payment-list or payment-date lookup endpoint. Paid status is shown from challan history.</p></div></section>
    <PaymentModal challan={selected} onClose={() => setSelected(null)} onSave={recordPayment} />
  </>;
}

function PaymentModal({ challan, onClose, onSave }) {
  const [form, setForm] = useState({ paymentReference: '', amount: '', paymentMethod: 'UPI', paymentDate: new Date().toISOString().slice(0, 10) });
  const [saving, setSaving] = useState(false);
  useEffect(() => { if (challan) setForm({ paymentReference: `CT-${Date.now().toString().slice(-8)}`, amount: String(challan.applicableFine), paymentMethod: 'UPI', paymentDate: new Date().toISOString().slice(0, 10) }); }, [challan]);
  if (!challan) return null;
  return <Modal title="Record payment" onClose={onClose}>
    <div className="payment-target"><span>{challan.challanNumber}</span><b>{challan.vehicleNumber} · {challan.violationType}</b><strong>{currency.format(challan.applicableFine)}</strong></div>
    <form className="form-grid" onSubmit={async (event) => { event.preventDefault(); setSaving(true); await onSave({ challanId: challan.id, ...form, amount: Number(form.amount) }); setSaving(false); }}>
      <Field label="Payment reference"><input required maxLength={100} value={form.paymentReference} onChange={(event) => setForm({ ...form, paymentReference: event.target.value })} /></Field>
      <Field label="Amount (INR)"><input required type="number" min="0.01" step="0.01" value={form.amount} onChange={(event) => setForm({ ...form, amount: event.target.value })} /></Field>
      <Field label="Payment method"><select value={form.paymentMethod} onChange={(event) => setForm({ ...form, paymentMethod: event.target.value })}><option>UPI</option><option>Card</option><option>Cash</option><option>Net Banking</option><option>Other</option></select></Field>
      <Field label="Payment date"><input required type="date" max={new Date().toISOString().slice(0, 10)} value={form.paymentDate} onChange={(event) => setForm({ ...form, paymentDate: event.target.value })} /></Field>
      <FormActions onCancel={onClose} saving={saving} saveLabel="Confirm payment" />
    </form>
  </Modal>;
}

function PageHeading({ eyebrow, title, description, action }) {
  return <div className="page-heading"><div><span className="eyebrow">{eyebrow}</span><h1>{title}</h1><p>{description}</p></div>{action && <div className="heading-action">{action}</div>}</div>;
}

function StatusPill({ status }) {
  const paid = status === 'PAID';
  return <span className={`status-pill ${paid ? 'status-paid' : 'status-unpaid'}`}><span />{paid ? 'Paid' : 'Pending'}</span>;
}

function EmptyState({ icon: Icon, title, detail, action }) {
  return <div className="empty-state"><span className="empty-icon"><Icon size={21} /></span><h3>{title}</h3><p>{detail}</p>{action}</div>;
}

function LoadingState() {
  return <div className="loading-state"><Spinner /><span>Loading enforcement records…</span></div>;
}

function Spinner({ small = false }) {
  return <span className={`spinner ${small ? 'spinner-small' : ''}`} aria-label="Loading" />;
}

function Modal({ title, onClose, children }) {
  useEffect(() => {
    const onKey = (event) => { if (event.key === 'Escape') onClose(); };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);
  return <div className="modal-scrim" role="presentation" onMouseDown={(event) => { if (event.target === event.currentTarget) onClose(); }}><section className="modal" role="dialog" aria-modal="true" aria-labelledby="modal-title"><div className="modal-header"><div><span className="eyebrow">CHALLAN TRACK</span><h2 id="modal-title">{title}</h2></div><button className="icon-button" aria-label="Close" onClick={onClose}><X size={19} /></button></div>{children}</section></div>;
}

function Field({ label, hint, className = '', children }) {
  return <label className={`field ${className}`}><span className="field-label">{label}</span>{children}{hint && <small className="field-hint">{hint}</small>}</label>;
}

function FormActions({ onCancel, saving, saveLabel }) {
  return <div className="form-actions"><button className="button button-quiet" type="button" onClick={onCancel}>Cancel</button><button className="button button-primary" type="submit" disabled={saving}>{saving ? <Spinner small /> : <Check size={16} />}{saving ? 'Saving…' : saveLabel}</button></div>;
}

function DetailRow({ label, value }) {
  return <div className="detail-row"><span>{label}</span><strong>{value}</strong></div>;
}

function NotFound() {
  return <EmptyState icon={Gauge} title="Page not found" detail="The requested section is not available." action={<Link className="button button-secondary" to="/">Return to overview</Link>} />;
}

export default App;