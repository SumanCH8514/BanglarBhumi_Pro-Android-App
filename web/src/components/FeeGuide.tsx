import React from 'react';
import { FileText, Shield, CheckCircle2, ExternalLink } from 'lucide-react';

interface FeeGuideProps {
  lang: 'en' | 'bn';
}

export const FeeGuide: React.FC<FeeGuideProps> = ({ lang }) => {
  return (
    <div className="bg-white rounded-2xl shadow-xl border border-slate-200 p-6 md:p-8 space-y-8">
      <div className="pb-6 border-b border-slate-100">
        <h2 className="text-xl font-bold text-slate-900 flex items-center gap-2">
          <FileText className="w-5 h-5 text-emerald-600" />
          <span>{lang === 'bn' ? 'পশ্চিমবঙ্গ ভূমি ও মিউটেশন সংক্রান্ত নির্দেশিকা' : 'West Bengal Land & Mutation Citizen Guide'}</span>
        </h2>
        <p className="text-sm text-slate-500 mt-1">
          {lang === 'bn'
            ? 'জমির নামপত্তন (মিউটেশন), কনভার্শন ও রেকর্ড সংশোধন সংক্রান্ত দরকারি নির্দেশাবলী'
            : 'Essential citizen manual for RoR mutation, land conversion, and rights verification'}
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="p-5 rounded-2xl bg-slate-50 border border-slate-200 space-y-3">
          <div className="w-9 h-9 rounded-xl bg-emerald-100 text-emerald-800 flex items-center justify-center font-bold">
            ১
          </div>
          <h3 className="font-bold text-slate-900 text-base">
            {lang === 'bn' ? 'মিউটেশন (Mutation)' : 'RoR Mutation (নামপত্তন)'}
          </h3>
          <p className="text-xs text-slate-600 leading-relaxed">
            {lang === 'bn'
              ? 'জমি ক্রয় বা উত্তরাধিকারসূত্রে প্রাপ্তির পর নিজের নামে খতিয়ান খোলার প্রক্রিয়া হলো মিউটেশন। রেজিস্ট্রি দলিলের কপি, খতিয়ান ও খাজনা রশিদ সাথে আবেদন করতে হয়।'
              : 'Process of recording the new owner’s name in the Record of Rights after purchase or inheritance. Requires registered deed copy, previous RoR, and rent receipts.'}
          </p>
        </div>

        <div className="p-5 rounded-2xl bg-slate-50 border border-slate-200 space-y-3">
          <div className="w-9 h-9 rounded-xl bg-emerald-100 text-emerald-800 flex items-center justify-center font-bold">
            ২
          </div>
          <h3 className="font-bold text-slate-900 text-base">
            {lang === 'bn' ? 'কনভার্শন (Conversion)' : 'Land Conversion (শ্রেণী পরিবর্তন)'}
          </h3>
          <p className="text-xs text-slate-600 leading-relaxed">
            {lang === 'bn'
              ? 'কৃষি জমিকে (শালী/ডাঙা) বাস্তু, বাণিজ্যিক বা শিল্প ক্ষেত্রে ব্যবহারের জন্য রূপান্তর করতে বি.এল.এন্ড এল.আর.ও (BL&LRO) অফিসে আবেদন করতে হয়।'
              : 'Official permission to convert agricultural land (Sali/Danga) into residential (Bastu) or commercial usage under the WBLR Act 1955 via BL&LRO office.'}
          </p>
        </div>

        <div className="p-5 rounded-2xl bg-slate-50 border border-slate-200 space-y-3">
          <div className="w-9 h-9 rounded-xl bg-emerald-100 text-emerald-800 flex items-center justify-center font-bold">
            ৩
          </div>
          <h3 className="font-bold text-slate-900 text-base">
            {lang === 'bn' ? 'ওয়ারিশান রেকর্ড (Warish)' : 'Warish Registration (উত্তরাধিকার)'}
          </h3>
          <p className="text-xs text-slate-600 leading-relaxed">
            {lang === 'bn'
              ? 'মূল রায়ত মারা গেলে তার বৈধ উত্তরাধিকারীদের নামে জমি ভাগ করে রেকর্ডভুক্ত করার জন্য পঞ্চায়েত বা মিউনিসিপ্যালিটির ওয়ারিশ সার্টিফিকেট জমা দিতে হয়।'
              : 'Transfer of title to legal heirs upon demise of the original recorded raiyat with valid Legal Heir certificate issued by Panchayat/Municipality.'}
          </p>
        </div>
      </div>

      <div className="p-6 bg-slate-50 rounded-2xl border border-slate-200 space-y-4">
        <h3 className="font-bold text-slate-900 text-sm flex items-center gap-2">
          <CheckCircle2 className="w-4 h-4 text-emerald-600" />
          <span>{lang === 'bn' ? 'মিউটেশন আবেদনের প্রয়োজনীয় নথিপত্র' : 'Checklist of Documents for Mutation Application'}</span>
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs text-slate-700">
          <div className="flex items-center gap-2 bg-white p-3 rounded-xl border border-slate-200/80">
            <span className="w-2 h-2 rounded-full bg-emerald-600 shrink-0"></span>
            <span>{lang === 'bn' ? 'রেজিস্ট্রি দলিলের সার্টিফায়েড কপি (Certified Deed)' : 'Registered Sale / Gift Deed copy'}</span>
          </div>
          <div className="flex items-center gap-2 bg-white p-3 rounded-xl border border-slate-200/80">
            <span className="w-2 h-2 rounded-full bg-emerald-600 shrink-0"></span>
            <span>{lang === 'bn' ? 'পূর্ববর্তী খতিয়ান কপি (Chain RoR / Parcha)' : 'Previous Chain Parcha / RoR copy'}</span>
          </div>
          <div className="flex items-center gap-2 bg-white p-3 rounded-xl border border-slate-200/80">
            <span className="w-2 h-2 rounded-full bg-emerald-600 shrink-0"></span>
            <span>{lang === 'bn' ? 'আপ-টু-ডেট জমির খাজনা রশিদ (Khajna Receipt)' : 'Up-to-date Land Revenue (Khajna) receipt'}</span>
          </div>
          <div className="flex items-center gap-2 bg-white p-3 rounded-xl border border-slate-200/80">
            <span className="w-2 h-2 rounded-full bg-emerald-600 shrink-0"></span>
            <span>{lang === 'bn' ? 'আধার কার্ড ও ছবি (Aadhaar & Photo)' : 'Aadhaar Card, Mobile No & Photo of applicant'}</span>
          </div>
        </div>
      </div>

      <div className="flex flex-col sm:flex-row items-center justify-between p-4 bg-emerald-50 rounded-xl border border-emerald-200 text-xs text-emerald-950 gap-3">
        <div className="flex items-center gap-2">
          <Shield className="w-4 h-4 text-emerald-700 shrink-0" />
          <span className="font-semibold">
            {lang === 'bn'
              ? 'অফিসিয়াল বাংলারভূমি ওয়েবসাইট: banglarbhumi.gov.in'
              : 'Official Banglarbhumi Portal: banglarbhumi.gov.in'}
          </span>
        </div>
        <a
          href="https://banglarbhumi.gov.in"
          target="_blank"
          rel="noopener noreferrer"
          className="inline-flex items-center gap-1 font-bold text-emerald-700 hover:text-emerald-900 underline"
        >
          <span>{lang === 'bn' ? 'সরকারি পোর্টাল ভিজিট করুন' : 'Open Official Portal'}</span>
          <ExternalLink className="w-3.5 h-3.5" />
        </a>
      </div>
    </div>
  );
};
