import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar';
import { HeroSection } from './components/HeroSection';
import { BrowseItemsView } from './views/BrowseItemsView';
import { ReportFoundView } from './views/ReportFoundView';
import { ReportLostView } from './views/ReportLostView';
import { MatchCenterView } from './views/MatchCenterView';
import { PickupDeskView } from './views/PickupDeskView';
import { MyActivityView } from './views/MyActivityView';
import { AdminDashboardView } from './views/AdminDashboardView';
import { ClaimModal } from './components/ClaimModal';
import { ItemDetailModal } from './components/ItemDetailModal';
import { SchedulePickupModal } from './components/SchedulePickupModal';
import { AuthModal } from './components/AuthModal';
import { DemoBanner } from './components/DemoBanner';
import { api } from './services/api';
import {
  User,
  FoundReport,
  LostReport,
  MatchCandidate,
  Claim,
  PickupAppointment,
  NotificationItem,
  CampusLocation,
  ItemCategory,
  AdminStats
} from './types';

export function App() {
  const [currentView, setCurrentView] = useState<string>('home');
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [authModalOpen, setAuthModalOpen] = useState<boolean>(false);

  // Data Stores
  const [locations, setLocations] = useState<CampusLocation[]>([]);
  const [categories, setCategories] = useState<ItemCategory[]>([]);
  const [foundItems, setFoundItems] = useState<FoundReport[]>([]);
  const [lostItems, setLostItems] = useState<LostReport[]>([]);
  const [matches, setMatches] = useState<MatchCandidate[]>([]);
  const [myClaims, setMyClaims] = useState<Claim[]>([]);
  const [myPickups, setMyPickups] = useState<PickupAppointment[]>([]);
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [unreadCount, setUnreadCount] = useState<number>(0);
  const [adminStats, setAdminStats] = useState<AdminStats | null>(null);

  // Modals state
  const [claimModalOpen, setClaimModalOpen] = useState<boolean>(false);
  const [targetFoundItem, setTargetFoundItem] = useState<FoundReport | null>(null);
  const [targetLostReportId, setTargetLostReportId] = useState<number | undefined>(undefined);

  const [detailModalOpen, setDetailModalOpen] = useState<boolean>(false);
  const [detailItem, setDetailItem] = useState<FoundReport | null>(null);

  const [scheduleModalOpen, setScheduleModalOpen] = useState<boolean>(false);
  const [targetClaimForPickup, setTargetClaimForPickup] = useState<Claim | null>(null);

  // Initial authentication & data load
  useEffect(() => {
    initApp();
  }, []);

  const initApp = async () => {
    try {
      // Default to student demo login
      const auth = await api.login('student@campus.edu', 'Password123!');
      setCurrentUser(auth.user);
      await loadAllData();
    } catch (e) {
      console.warn('Backend login fallback, loading local demo state');
      await loadAllData();
    }
  };

  const loadAllData = async () => {
    try {
      const [locs, cats, found, lost, myMatches, claims, pickups, notifs, unread] = await Promise.all([
        api.getLocations().catch(() => []),
        api.getCategories().catch(() => []),
        api.getFoundReports().catch(() => []),
        api.getLostReports().catch(() => []),
        api.getMyMatches().catch(() => []),
        api.getMyClaims().catch(() => []),
        api.getMyPickups().catch(() => []),
        api.getNotifications().catch(() => []),
        api.getUnreadNotificationCount().catch(() => 0)
      ]);

      setLocations(locs);
      setCategories(cats);
      setFoundItems(found);
      setLostItems(lost);
      setMatches(myMatches);
      setMyClaims(claims);
      setMyPickups(pickups);
      setNotifications(notifs);
      setUnreadCount(unread);

      // Load admin stats if staff/admin
      api.getAdminStats().then(setAdminStats).catch(() => {});
    } catch (err) {
      console.error('Error fetching data:', err);
    }
  };

  const handleSwitchUser = async (email: string) => {
    try {
      const auth = await api.login(email, 'Password123!');
      setCurrentUser(auth.user);
      await loadAllData();
    } catch (e) {
      console.error('Failed to switch user:', e);
    }
  };

  const handleMarkNotificationRead = async (id: number) => {
    await api.markNotificationAsRead(id);
    setNotifications(prev => prev.map(n => n.id === id ? { ...n, isRead: true } : n));
    setUnreadCount(prev => Math.max(0, prev - 1));
  };

  const openClaimModal = (foundItem: FoundReport, lostReportId?: number) => {
    setTargetFoundItem(foundItem);
    setTargetLostReportId(lostReportId);
    setClaimModalOpen(true);
  };

  const openClaimFromMatch = (foundReportId: number, lostReportId?: number) => {
    const item = foundItems.find(f => f.id === foundReportId) || {
      id: foundReportId,
      referenceId: 'LF-2026-MATCH',
      finderName: 'Campus Community',
      title: 'Matching Item',
      category: 'Bags',
      locationId: 1,
      locationName: 'Central Library',
      foundDate: new Date().toISOString(),
      possessionStatus: 'STORED_IN_OFFICE',
      status: 'VERIFIED',
      hasPrivateDetails: true,
      createdAt: new Date().toISOString()
    } as FoundReport;

    openClaimModal(item, lostReportId);
  };

  const openItemDetail = (item: FoundReport) => {
    setDetailItem(item);
    setDetailModalOpen(true);
  };

  const openSchedulePickup = (claim: Claim) => {
    setTargetClaimForPickup(claim);
    setScheduleModalOpen(true);
  };

  return (
    <div className="min-h-screen bg-slate-50 flex flex-col selection:bg-brand-500 selection:text-white pb-20">
      {/* Top Navigation */}
      <Navbar
        currentView={currentView}
        onNavigate={setCurrentView}
        currentUser={currentUser}
        onSwitchUser={handleSwitchUser}
        onOpenAuth={() => setAuthModalOpen(true)}
        notifications={notifications}
        unreadCount={unreadCount}
        onMarkRead={handleMarkNotificationRead}
        matchCount={matches.length}
      />

      {/* Main Content Area */}
      <main className="flex-1">
        {currentView === 'home' && (
          <div>
            <HeroSection
              onNavigate={setCurrentView}
              stats={{
                activeFound: foundItems.length,
                activeLost: lostItems.length,
                recoveryRate: adminStats?.recoveryRatePercentage || 94.2
              }}
            />
            {/* Quick Catalog Preview on Home */}
            <BrowseItemsView
              foundItems={foundItems.slice(0, 6)}
              lostItems={lostItems.slice(0, 6)}
              locations={locations}
              categories={categories}
              onClaimItem={openClaimModal}
              onViewDetails={openItemDetail}
              onNavigate={setCurrentView}
            />
          </div>
        )}

        {currentView === 'browse' && (
          <BrowseItemsView
            foundItems={foundItems}
            lostItems={lostItems}
            locations={locations}
            categories={categories}
            onClaimItem={openClaimModal}
            onViewDetails={openItemDetail}
            onNavigate={setCurrentView}
          />
        )}

        {currentView === 'report-found' && (
          <ReportFoundView
            locations={locations}
            categories={categories}
            onSuccess={async () => {
              await loadAllData();
            }}
            onNavigate={setCurrentView}
          />
        )}

        {currentView === 'report-lost' && (
          <ReportLostView
            locations={locations}
            categories={categories}
            onSuccess={async () => {
              await loadAllData();
            }}
            onNavigate={setCurrentView}
          />
        )}

        {currentView === 'matches' && (
          <MatchCenterView
            matches={matches}
            onClaimItem={openClaimFromMatch}
            onNavigate={setCurrentView}
          />
        )}

        {currentView === 'pickup' && (
          <PickupDeskView
            pickups={myPickups}
            currentUser={currentUser}
            onRefresh={loadAllData}
            onNavigate={setCurrentView}
          />
        )}

        {currentView === 'my-activity' && (
          <MyActivityView
            myLost={lostItems.filter(l => currentUser && l.ownerId === currentUser.id)}
            myFound={foundItems.filter(f => currentUser && f.finderId === currentUser.id)}
            myClaims={myClaims}
            myPickups={myPickups}
            onNavigate={setCurrentView}
            onSchedulePickup={openSchedulePickup}
          />
        )}

        {currentView === 'admin' && (
          <AdminDashboardView
            stats={adminStats}
            claims={myClaims}
            onRefresh={loadAllData}
          />
        )}
      </main>

      {/* Floating Portfolio Demo Mode Toolbar */}
      <DemoBanner
        currentUser={currentUser}
        onSwitchUser={handleSwitchUser}
        onNavigate={setCurrentView}
      />

      {/* Modals */}
      <ClaimModal
        isOpen={claimModalOpen}
        item={targetFoundItem}
        lostReportId={targetLostReportId}
        onClose={() => setClaimModalOpen(false)}
        onSuccess={async () => {
          await loadAllData();
          setCurrentView('my-activity');
        }}
      />

      <ItemDetailModal
        isOpen={detailModalOpen}
        item={detailItem}
        onClose={() => setDetailModalOpen(false)}
        onClaim={openClaimModal}
      />

      <SchedulePickupModal
        isOpen={scheduleModalOpen}
        claim={targetClaimForPickup}
        onClose={() => setScheduleModalOpen(false)}
        onSuccess={async () => {
          await loadAllData();
          setCurrentView('pickup');
        }}
      />

      <AuthModal
        isOpen={authModalOpen}
        onClose={() => setAuthModalOpen(false)}
        onSuccess={async (user) => {
          setCurrentUser(user);
          await loadAllData();
        }}
      />
    </div>
  );
}

export default App;
