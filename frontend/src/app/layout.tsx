import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Stephen Darcy",
  description:
    "Software engineer focused on polished interfaces, reliable services, and secure delivery practices.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
