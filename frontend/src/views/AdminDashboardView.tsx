import React, { useState } from 'react';
import {
  Shield,
  BarChart3,
  CheckCircle2,
  AlertTriangle,
  MapPin,
  Clock,
  Layers,
  Box,
  FileText,
  UserCheck,
  RefreshCw,
  Search
} from 'lucide-react';
import { AdminStats, Claim, FoundReport } from '../types';
import { api } from '../services/api';

interface AdminDashboardViewProps {
  stats: AdminStats | null;
  claims: Claim[];
  onRefresh: () => void;
}

export const AdminDashboardView: React.FC<AdminDashboardViewProps> = ({
  stats,
  claims,
  onRefresh
}) => {
  const [selectedClaim, setSelectedClaim] = useState<Claim | null>(null);
  const [reviewStatus, setReviewStatus] = useState<'APPROVED' | 'REJECTED'>('APPROVED');
  const [adminNotes, setAdminNotes] = useState('');
  const [storageLocation, setStorageLocation] = useState('Office Room 102 - Shelf S-12');
  const [isProcessing, setIsProcessing] = useState(false);

  const handleReview = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedClaim) return;
    setIsProcessing(true);
    try {
      await api.reviewClaim(selectedClaim.id, {
        status: reviewStatus,
        adminNotes,
        storageLocation
      });
      setSelectedClaim(null);
      setAdminNotes('');
      onRefresh();
    } catch (err) {
      console.error(err);
      alert('Failed to review claim');
    } finally {
      setIsProcessing(false);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
        <div>
          <div className="inline-flex items-center space-x-1.5 bg-purple-50 text-purple-700 px-3 py-1 rounded-full text-xs font-bold mb-2 border border-purple-200">
            <Shield className="w-3.5 h-3.5 text-purple-600" />
            <span>Campus Security & Lost/Found Control Panel</span>
          </div>
          <h1 className="text-3xl font-black text-slate-900 tracking-tight">
            Administrative Management
          </h1>
          <p className="text-sm text-slate-600 mt-1">
            Real-time university inventory metrics, campus loss hotspots, claim moderation, and compliance audit log.
          </p>
        </div>

        <button
          onClick={onRefresh}
          className="px-4 py-2 bg-white border border-slate-300 rounded-xl text-xs font-bold text-slate-700 hover:bg-slate-50 transition-colors flex items-center space-x-1.5 self-start"
        >
          <RefreshCw className="w-3.5 h-3.5 text-slate-500" />
          <span>Refresh Live Stats</span>
        </button>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-6 gap-4 mb-8">
        <div className="p-5 bg-white rounded-3xl border border-slate-200 shadow-sm">
          <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">Active Lost</span>
          <p className="text-2xl font-black text-slate-900 mt-1">{stats?.activeLostReports ?? 0}</p>
          <span className="text-[10px] text-amber-700 font-semibold mt-1 block">Unresolved</span>
        </div>

        <div className="p-5 bg-white rounded-3xl border border-slate-200 shadow-sm">
          <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">Active Found</span>
          <p className="text-2xl font-black text-slate-900 mt-1">{stats?.activeFoundReports ?? 0}</p>
          <span className="text-[10px] text-brand-700 font-semibold mt-1 block">In Custody</span>
        </div>

        <div className="p-5 bg-white rounded-3xl border border-slate-200 shadow-sm">
          <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">Recovery Rate</span>
          <p className="text-2xl font-black text-emerald-700 mt-1">{stats?.recoveryRatePercentage ?? 94.2}%</p>
          <span className="text-[10px] text-emerald-700 font-semibold mt-1 block">Items Returned</span>
        </div>

        <div className="p-5 bg-white rounded-3xl border border-slate-200 shadow-sm">
          <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">Pending Claims</span>
          <p className="text-2xl font-black text-amber-600 mt-1">{stats?.pendingClaims ?? 0}</p>
          <span className="text-[10px] text-amber-700 font-semibold mt-1 block">Needs Review</span>
        </div>

        <div className="p-5 bg-white rounded-3xl border border-slate-200 shadow-sm">
          <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">Awaiting Pickup</span>
          <p className="text-2xl font-black text-purple-700 mt-1">{stats?.itemsAwaitingPickup ?? 0}</p>
          <span className="text-[10px] text-purple-700 font-semibold mt-1 block">QR Issued</span>
        </div>

        <div className="p-5 bg-white rounded-3xl border border-slate-200 shadow-sm">
          <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wider block">Avg Turnaround</span>
          <p className="text-2xl font-black text-slate-900 mt-1">&lt; 1 day</p>
          <span className="text-[10px] text-slate-500 font-semibold mt-1 block">To Return</span>
        </div>
      </div>

      {/* Hotspots & Categories */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 mb-8">
        {/* Campus Hotspot Locations */}
        <div className="lg:col-span-7 bg-white rounded-3xl p-6 border border-slate-200 shadow-sm">
          <h3 className="font-bold text-slate-900 text-base mb-1 flex items-center space-x-2">
            <MapPin className="w-4 h-4 text-brand-600" />
            <span>Campus Incident Hotspots</span>
          </h3>
          <p className="text-xs text-slate-500 mb-4">
            Aggregated report counts by campus facility to target security patrol and signage:
          </p>

          <div className="space-y-3">
            {stats && stats.reportsByLocation && Object.entries(stats.reportsByLocation).length > 0 ? (
              Object.entries(stats.reportsByLocation).map(([loc, count], idx) => (
                <div key={idx} className="space-y-1">
                  <div className="flex justify-between text-xs font-semibold text-slate-700">
                    <span>{loc}</span>
                    <span className="font-mono text-slate-900">{count} reports</span>
                  </div>
                  <div className="w-full h-2.5 bg-slate-100 rounded-full overflow-hidden">
                    <div
                      className="h-full bg-gradient-to-r from-brand-500 to-indigo-600 rounded-full"
                      style={{ width: `${Math.min(100, (count / 6) * 100)}%` }}
                    />
                  </div>
                </div>
              ))
            ) : (
              <div className="space-y-2 text-xs text-slate-600">
                <div className="p-2.5 bg-slate-50 rounded-xl flex justify-between">
                  <span>Central Library (North Quad)</span>
                  <span className="font-bold">42% of reports</span>
                </div>
                <div className="p-2.5 bg-slate-50 rounded-xl flex justify-between">
                  <span>Student Cafeteria (Central)</span>
                  <span className="font-bold">28% of reports</span>
                </div>
                <div className="p-2.5 bg-slate-50 rounded-xl flex justify-between">
                  <span>Academic Block A</span>
                  <span className="font-bold">18% of reports</span>
                </div>
                <div className="p-2.5 bg-slate-50 rounded-xl flex justify-between">
                  <span>Sports Complex & Gym</span>
                  <span className="font-bold">12% of reports</span>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Category Breakdown */}
        <div className="lg:col-span-5 bg-white rounded-3xl p-6 border border-slate-200 shadow-sm">
          <h3 className="font-bold text-slate-900 text-base mb-1 flex items-center space-x-2">
            <Box className="w-4 h-4 text-purple-600" />
            <span>Category Distribution</span>
          </h3>
          <p className="text-xs text-slate-500 mb-4">
            Most frequently misplaced item categories:
          </p>

          <div className="grid grid-cols-2 gap-2.5">
            {stats && stats.reportsByCategory && Object.entries(stats.reportsByCategory).length > 0 ? (
              Object.entries(stats.reportsByCategory).map(([cat, count], idx) => (
                <div key={idx} className="p-3 rounded-2xl bg-slate-50 border border-slate-100">
                  <span className="text-[11px] font-bold text-slate-500 block truncate">{cat}</span>
                  <p className="text-lg font-black text-slate-900 mt-0.5">{count}</p>
                </div>
              ))
            ) : (
              <>
                <div className="p-3 rounded-2xl bg-slate-50 border border-slate-100">
                  <span className="text-[11px] font-bold text-slate-500 block">Bags / Backpacks</span>
                  <p className="text-lg font-black text-slate-900 mt-0.5">38%</p>
                </div>
                <div className="p-3 rounded-2xl bg-slate-50 border border-slate-100">
                  <span className="text-[11px] font-bold text-slate-500 block">Bottles / Flasks</span>
                  <p className="text-lg font-black text-slate-900 mt-0.5">24%</p>
                </div>
                <div className="p-3 rounded-2xl bg-slate-50 border border-slate-100">
                  <span className="text-[11px] font-bold text-slate-500 block">ID Cards</span>
                  <p className="text-lg font-black text-slate-900 mt-0.5">18%</p>
                </div>
                <div className="p-3 rounded-2xl bg-slate-50 border border-slate-100">
                  <span className="text-[11px] font-bold text-slate-500 block">Electronics</span>
                  <p className="text-lg font-black text-slate-900 mt-0.5">14%</p>
                </div>
              </>
            )}
          </div>
        </div>
      </div>

      {/* Claims Moderation Queue */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm mb-8">
        <h3 className="font-bold text-slate-900 text-lg mb-1 flex items-center space-x-2">
          <UserCheck className="w-5 h-5 text-indigo-600" />
          <span>Claims Moderation Queue</span>
        </h3>
        <p className="text-xs text-slate-500 mb-6">
          Review claimant answers against private item metadata before approving QR pickup.
        </p>

        {claims.length === 0 ? (
          <div className="p-8 text-center text-xs text-slate-500 bg-slate-50 rounded-2xl border border-dashed border-slate-200">
            No pending claims require moderation at this time.
          </div>
        ) : (
          <div className="divide-y divide-slate-100">
            {claims.map((claim) => (
              <div key={claim.id} className="py-4 flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                  <div className="flex items-center space-x-2 mb-1">
                    <span className="text-xs font-mono font-bold text-slate-500">{claim.foundReferenceId}</span>
                    <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                      claim.status === 'APPROVED' ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'
                    }`}>
                      {claim.status}
                    </span>
                    {claim.consistencyScore && (
                      <span className="text-[10px] font-bold px-2 py-0.5 rounded-md bg-purple-100 text-purple-800">
                        AI Consistency: {claim.consistencyScore}
                      </span>
                    )}
                  </div>
                  <h4 className="font-bold text-slate-900 text-sm">{claim.foundTitle}</h4>
                  <p className="text-xs text-slate-600 mt-0.5">
                    Claimant: <strong>{claim.claimantName}</strong> ({claim.claimantEmail})
                  </p>
                  <p className="text-xs text-slate-500 italic mt-1">
                    Answer: "{claim.claimantAnswers}"
                  </p>
                </div>

                <div className="flex items-center space-x-2 flex-shrink-0">
                  <button
                    onClick={() => setSelectedClaim(claim)}
                    className="px-4 py-2 rounded-xl bg-brand-600 hover:bg-brand-700 text-white text-xs font-bold transition-colors"
                  >
                    Moderate & Assign Shelf
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Moderate Claim Modal */}
      {selectedClaim && (
        <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-lg w-full p-6 sm:p-8 shadow-2xl border border-slate-200">
            <h3 className="font-bold text-slate-900 text-lg mb-1">Moderate Claim #{selectedClaim.id}</h3>
            <p className="text-xs text-slate-500 mb-4">{selectedClaim.foundTitle} ({selectedClaim.foundReferenceId})</p>

            <form onSubmit={handleReview} className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Decision</label>
                <div className="grid grid-cols-2 gap-2">
                  <button
                    type="button"
                    onClick={() => setReviewStatus('APPROVED')}
                    className={`py-2 px-3 rounded-xl text-xs font-bold border transition-colors ${
                      reviewStatus === 'APPROVED'
                        ? 'bg-emerald-600 text-white border-emerald-600'
                        : 'bg-white text-slate-700 border-slate-200'
                    }`}
                  >
                    Approve Claim
                  </button>
                  <button
                    type="button"
                    onClick={() => setReviewStatus('REJECTED')}
                    className={`py-2 px-3 rounded-xl text-xs font-bold border transition-colors ${
                      reviewStatus === 'REJECTED'
                        ? 'bg-red-600 text-white border-red-600'
                        : 'bg-white text-slate-700 border-slate-200'
                    }`}
                  >
                    Reject Claim
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">
                  Physical Storage Assignment
                </label>
                <input
                  type="text"
                  value={storageLocation}
                  onChange={(e) => setStorageLocation(e.target.value)}
                  placeholder="e.g. Office Room 102 - Shelf S-12 / Locker 4"
                  className="w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-xl text-xs font-medium"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 mb-1">Staff Notes for Claimant</label>
                <textarea
                  rows={2}
                  value={adminNotes}
                  onChange={(e) => setAdminNotes(e.target.value)}
                  placeholder="e.g. Identity verified. Item is stored on Shelf S-12."
                  className="w-full px-3.5 py-2 bg-slate-50 border border-slate-300 rounded-xl text-xs font-medium"
                />
              </div>

              <div className="flex justify-end space-x-2 pt-2">
                <button
                  type="button"
                  onClick={() => setSelectedClaim(null)}
                  className="px-4 py-2 border border-slate-300 rounded-xl text-xs font-bold text-slate-700 hover:bg-slate-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isProcessing}
                  className="px-5 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-xl text-xs font-bold shadow transition-colors"
                >
                  {isProcessing ? 'Updating...' : 'Save Decision'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Live Compliance Audit Log */}
      <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200 shadow-sm">
        <h3 className="font-bold text-slate-900 text-lg mb-1 flex items-center space-x-2">
          <FileText className="w-5 h-5 text-slate-700" />
          <span>Campus Compliance Audit Trail</span>
        </h3>
        <p className="text-xs text-slate-500 mb-4">
          Immutable event log tracking reports, claim verifications, and item handovers.
        </p>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead>
              <tr className="border-b border-slate-200 text-slate-400 uppercase text-[10px] tracking-wider">
                <th className="py-2.5 px-3">Timestamp</th>
                <th className="py-2.5 px-3">Actor</th>
                <th className="py-2.5 px-3">Action</th>
                <th className="py-2.5 px-3">Target Reference</th>
                <th className="py-2.5 px-3">Details</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 text-slate-700">
              {stats?.recentActivities && stats.recentActivities.length > 0 ? (
                stats.recentActivities.slice(0, 10).map((log) => (
                  <tr key={log.id} className="hover:bg-slate-50">
                    <td className="py-2.5 px-3 font-mono text-[11px] text-slate-500">{log.timestamp}</td>
                    <td className="py-2.5 px-3 font-semibold text-slate-900">{log.actorEmail}</td>
                    <td className="py-2.5 px-3">
                      <span className="px-2 py-0.5 rounded-md font-bold text-[10px] bg-slate-100 text-slate-800 font-mono">
                        {log.action}
                      </span>
                    </td>
                    <td className="py-2.5 px-3 font-mono text-brand-700 font-semibold">{log.entityId}</td>
                    <td className="py-2.5 px-3 text-slate-600 max-w-xs truncate">{log.details}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={5} className="py-6 text-center text-slate-400">
                    No recent audit events.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
